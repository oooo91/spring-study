package hello.container;

import hello.servlet.HelloServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

/**
 * 서블릿 객체 초기화 (등록)
 * 순수 서블릿 코드다.
 * 서블릿 컨테이너에 등록되면 /hello-servlet 호출 시 실행되는 서블릿 객체
 */
public class AppInitV1Servlet implements AppInit {

    @Override
    public void onStartUp(ServletContext servletContext) {
        System.out.println("AppInitV1Servlet.onStartup");

        //서블릿 컨테이너에 서블릿 객체 등록
        ServletRegistration.Dynamic helloServlet =
            servletContext.addServlet("helloServlet", new HelloServlet());

        /**
         * 매핑하기 /hello-servlet 요청오면 HelloServlet() 컨트롤 실행
         */
        helloServlet.addMapping("/hello-servlet");
    }
}
