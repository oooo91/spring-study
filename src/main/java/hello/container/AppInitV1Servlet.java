package hello.container;

import hello.servlet.HelloServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

/** 서블릿 생성 */
public class AppInitV1Servlet implements AppInit {

    @Override
    public void onStartUp(ServletContext servletContext) {
        System.out.println("AppInitV1Servlet.onStartup");
        //순수 서블릿 코드 등록 (객체 생성 -> 서블릿 컨테이너에 들어가 -> /hello-servlet 호출 시 -> 객체의 함수 실행)
        ServletRegistration.Dynamic helloServlet =
            servletContext.addServlet("helloServlet", new HelloServlet());
        //매핑
        helloServlet.addMapping("/hello-servlet");
    }
}
