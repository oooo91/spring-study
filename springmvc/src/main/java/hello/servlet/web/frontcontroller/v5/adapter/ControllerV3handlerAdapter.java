package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 어뎁터 -> 이전 v3에서 프론트 컨트롤러가 컨트롤러를 직접 호출했던 것을 이제 어뎁터가 담당하게 된다.
 */
public class ControllerV3handlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3); //v3를 구현한 컨트롤러면 true
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
                                                                throws ServletException, IOException {
        //타입 변환
        //캐스팅해도 된다 -> 앞서 supports에서 한 번 거르기 때문에 true인 경우만 handler() 함수가 호출될 것이다.
        ControllerV3 controller = (ControllerV3) handler;

        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controller.process(paramMap);

        return mv;
    }

    private static Map<String, String> createParamMap(HttpServletRequest request) {

        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
