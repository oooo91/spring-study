package hello.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// /hello 요청 -> WAS가 REQUEST, RESPONSE 만들어서 HelloServlet(서블릿 객체) 호출 -> 호출하면 해당 객체의 service가 호출
@WebServlet(name = "helloServlet", urlPatterns = "/hello") //서블릿 어노테이션 (name:서블릿 이름, urlPatterns : URL 매핑)
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
                                                throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + request); //request = org.apache.catalina.connector.RequestFacade@5e113861 톰캣 쪽 라이브러리
        System.out.println("response = " + response); //response = org.apache.catalina.connector.ResponseFacade@50d6cdd3
        
        String username= request.getParameter("username"); //query parameter 조회
        System.out.println(username);
        
        //////header 안에 들어가는 정보
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        
        //////body 안에 들어가는 정보
        response.getWriter().write("hello " + username);
    }
}

/*
HttpServletRequest는 다른 여러 기능도 함께 제공한다.

1. 임시 저장소 기능
해당 HTTP 요청이 시작부터 끝날 때까지 유지되는 임시 저장소 기능
저장 : request.setAttribute(name, value)
조회 : request.getAttribute(name)

2. 세션 관리 기능
request.getSession(create : true)
가장 중요한 결론은 HttpServletRequest/Response는 HTTP 요청/응답 메시지를 편리하게 사용하도록 도와주는 객체라는 점이다.
*/
