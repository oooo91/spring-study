package hello.servlet.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.servlet.basic.HelloData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JSON 형식으로 데이터 보내기
 */
@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    /**
     HTTP 응답으로 JSON 반환할 때는 CONTENT-TYPE을 APPLICATION/JSON 으로 지정해야한다.
     JACKSON 라이브러리가 제공하는 ObjectMapper.writeValueAsString()를 사용하면 객체를 JSON 문자열로 변경할 수 있다.

     참고
     APPLICATION/JSON은 스펙상 utf-8을 사용하도록 정의되어있어서 굳이 charset=utf-8과 같은 추가 파라미터는 의미 없다.
     */

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
                                                throws ServletException, IOException {
        //Content-Type : application/json
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("kim");
        helloData.setAge(20);

        //JSON(문자열)으로 변환 ㄱㄱ -> {"username" : "kim", "age" : 20}
        String result = objectMapper.writeValueAsString(helloData);
        response.getWriter().write(result);
    }
}
