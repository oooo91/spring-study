package hello.container;

import hello.spring.HelloController;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.lang.annotation.Annotation;

/**
 * 애플리케이션 초기화 -> 서블릿 컨테이너에 스프링 컨테이너 등록하기
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
