package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {
    /**
     MyHttpRequestHandler 를 실행하면서 사용된 객체는 다음과 같다.
     HandlerMapping = BeanNameUrlHandlerMapping
     HandlerAdapter = HttpRequestHandlerAdapter
     */
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
                                                throws ServletException, IOException {
        System.out.println("MyHttpRequestHandler.handleRequest");
    }
}
