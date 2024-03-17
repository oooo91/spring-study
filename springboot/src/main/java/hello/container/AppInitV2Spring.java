package hello.container;

import hello.spring.HelloController;
import jakarta.servlet.ServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 서블릿 객체 초기화 (등록)
 * 서블릿 컨테이넝 + 스프링 컨테이너 둘 다 사용
 * 디스패처 서블릿으로 서블릿과 스프링 MVC를 스프링 부트없이 연결
 * HelloServlet도 testServlet도 필요없이 단 하나의 DispatcherServlet이 컨테이너에서 요청에 매칭되는 컨트롤을 찾도록
 * 디스패처를 서블릿 컨테이너에 등록한다.
 */
public class AppInitV2Spring implements AppInit {

    @Override
    public void onStartUp(ServletContext servletContext) {
        System.out.println("AppInitV2Spring.onStartup");

        //스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(HelloController.class);

        //스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);

        //디스패처 서블릿을 서블릿 컨테이너에 등록
        // /spring/* 요청이 디스패처 서블릿을 통하도록 설정
        servletContext.addServlet("dispatcherV2", dispatcher)
                .addMapping("/spring/*");
    }
}
