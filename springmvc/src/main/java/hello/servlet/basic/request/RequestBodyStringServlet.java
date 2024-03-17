package hello.servlet.basic.request;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 JSON으로 데이터 받기 테스트 전 - 단순 텍스트 형식으로는 어떻게 받나
 */
@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
                                                    throws ServletException, IOException {
        //CONTENT를 BYTE CODE로 받음
        //추가적으로, FORM 을 통한 POST도 BODY를 통해 데이터가 전송되므로 INPUTSTREAM으로 데이터를 읽어올 수 있으나, getParameter이 존재하기 때문에 이로 조회하는 게 보통이다.
        InputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); //바이트 코드를 인코딩

        System.out.println("messageBody = " + messageBody);
        response.getWriter().write("ok");
    }

    /**
     HTTP 요청 데이터 - 개요
     HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법을 알아보자.
     주로 다음 3가지 방법을 사용한다.

     1. GET - 쿼리 파라미터
     /url?username=hello&age=20
     메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
     예) 검색, 필터, 페이징등에서 많이 사용하는 방식

     2. POST - HTML Form
     content-type: application/x-www-form-urlencoded
     메시지 바디에 쿼리 파리미터 형식으로 전달 username=hello&age=20
     --> get이나 post나 쿼리 파라미터 형식으로 전달되므로 위의 /hello에 get, post 다 보낼 수 있다.
     --> 즉 클라이언트(브라우저) 입장에서는 두 방식에 차이가 있으나 서버는 둘의 형식이 동일하므로 request.getParameter()로 편리하게 구분없이 조회할 수 있다.
     --> 결론적으로 getParamater은 get과 post 방식 둘 다 지원한다.
     예) 회원 가입, 상품 주문, HTML Form 사용

     CONTENT-TYPE은 HTTP 메시지 바디의 데이터 형식을 지정한다.
     GET은 바디를 사용하지 않으므로 CONTENT-TYPE이 없으나, POST는 APPLICATION/X-WWW-FORM-URLENCODED 라고 지정해야한다.

     3. API 메시지 바디
     HTTP message body에 데이터를 직접 담아서 요청
     HTTP API에서 주로 사용, JSON, XML, TEXT
     데이터 형식은 주로 JSON 사용
     POST, PUT, PATCH
     */
}
