package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;

import java.util.Map;

/**
 * 매개변수가 (HttpServletRequest request, HttpServletResponse response) 가 x
 * 서블릿에 종속적이지 않다.
 */
public interface ControllerV3 {
    ModelView process(Map<String, String> paramMap);
}
