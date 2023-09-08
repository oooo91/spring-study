package hello.embed;

import hello.spring.HelloConfig;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 내장 톰캣 + 스프링 연결
 */
public class EmbedTomcatSpringMain {
    public static void main(String[] args) {
        System.out.println("EmbedTomcatSpringMain.main");

        //톰캣 설정
        Tomcat tomcat = new Tomcat();

        //연결 설정
        Connector connector = new Connector(); //ctrl alt v = 자동 생성
        connector.setPort(8080);
        tomcat.setConnector(connector);

        //스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext =
                new AnnotationConfigWebApplicationContext();
        appContext.register(HelloConfig.class);

        //스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);

        //디스패처 서블릿 등록
        Context context = tomcat.addContext("", "/");
        tomcat.addServlet("", "dispatcher", dispatcher);
        context.addServletMappingDecoded("/", "dispatcher"); // /경로로 오면 디스패처 서블릿 호출
    }
}
