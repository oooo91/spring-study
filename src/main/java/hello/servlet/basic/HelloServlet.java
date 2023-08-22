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


/*
HTTP 요청 데이터 - 개요
HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법을 알아보자.
주로 다음 3가지 방법을 사용한다.

GET - 쿼리 파라미터
/url?username=hello&age=20
메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
예) 검색, 필터, 페이징등에서 많이 사용하는 방식

POST - HTML Form
content-type: application/x-www-form-urlencoded
메시지 바디에 쿼리 파리미터 형식으로 전달 username=hello&age=20
예) 회원 가입, 상품 주문, HTML Form 사용

HTTP message body에 데이터를 직접 담아서 요청
HTTP API에서 주로 사용, JSON, XML, TEXT
데이터 형식은 주로 JSON 사용
POST, PUT, PATCH

 */