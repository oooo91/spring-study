package hello.container;

import hello.spring.HelloController;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * MVC에서 제공한다 -> WebApplicationInitializer 만 구현하면 애플리케이션 초기화 (서블릿 등록) 알아서 해준다.

 * 서블릿 컨테이너 초기화 과정은 상당히 번거롭고 반복되는 작업이다.
 * 스프링 MVC는 이러한 서블릿 컨테이너 초기화 작업을 이미 만들어두었다.
 * 덕분에 개발자는 서블릿 컨테이너 초기화 과정은 생략하고, 애플리케이션 초기화 코드만 작성하면 된다.
 * 스프링이 지원하는 애플리케이션 초기화를 사용하려면 다음 인터페이스를 구현하면 된다.

 * WebApplicationInitializer 는 스프링이 이미 만들어둔 애플리케이션 초기화 인터페이스이다.
 * 여기서도 디스패처 서블릿을 새로 만들어서 등록하는데, 이전 코드에서는 dispatcherV2 라고 했고, 여기서는 dispatcherV3 라고 해주었다.
 * 참고로 이름이 같은 서블릿을 등록하면 오류가 발생한다.
 * servlet.addMapping("/") 코드를 통해 모든 요청이 해당 서블릿을 타도록 했다.
 * 따라서 다음과 같이 요청하면 해당 디스패처 서블릿을 통해 /hello-spring 이 매핑된 컨트롤러 메서드가 호출된다.

 * 일반적으로는 스프링 컨테이너(빈 모음)를 하나 만들고, 디스패처 서블릿(스프링 컨테이너에서 컨트롤 찾기)도 하나만 만든다.
 * 그리고 디스패처 서블릿의 경로 매핑도 / 로 해서 하나의 디스패처 서블릿을 통해서 모든 것을 처리하도록 한다.
 */
public class AppInitV3SpringMVC implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("AppInitV3Spring.onStartup");

        //스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(HelloController.class);

        //스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);

        //모든 요청이 디스패처 서블릿을 통하도록 설정
        servletContext.addServlet("dispatcherV3", dispatcher)
                .addMapping("/");
    }
}
