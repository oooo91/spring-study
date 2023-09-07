package hello.container;

import hello.spring.HelloController;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitV3SpringMVC implements WebApplicationInitializer {
    /**
     * MVC에서 제공한다 -> WebApplicationInitializer 만 구현하면 초기화 알아서 해준다.
     */
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
