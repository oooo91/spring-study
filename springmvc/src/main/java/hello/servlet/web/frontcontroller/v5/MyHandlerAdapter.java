package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyHandlerAdapter {
    boolean supports(Object handler); //해당 컨트롤러(핸들러)를 지원할 수 있는가

    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) //컨트롤러 호출 후 반환할 ModelView
                                                        throws ServletException, IOException;
}
