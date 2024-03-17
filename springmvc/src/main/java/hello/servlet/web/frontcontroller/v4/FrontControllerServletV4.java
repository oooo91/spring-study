package hello.servlet.web.frontcontroller.v4;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet(name = "frontControllerServletV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletV4 extends HttpServlet {

    /**
     * v5가 개선해야할 문제 -> 컨트롤러 형식이 정해져있다.
     * 가령 ControllerV4를 사용하다가 ControllerV1 형식으로 (request, response) 데이터를 받고 싶을 때가 있을 것이다.
     * 근데 쓸 수 없음 아래 코드 보셈 ControllerV4 떡하니 인터페이스 타입 고정되어있는데 쓸 수 있겠냐
     * 따라서 어떤 컨트롤러든 호출할 수 있는 유연한 컨트롤러 인터페이스가 필요하다. -> 어뎁터 패턴 사용하자

     * 어뎁터 패턴이란
     * ControllerV3과 ControllerV4가 호환되도록 하는 패턴 (프론트 컨트롤러가 다양한 방식의 컨트롤러를 처리할 수 있도록 한다.)
     * 이제부터 프론트 컨트롤러는 직접 컨트롤러(핸들러)를 호출하는 것이 아니라 어뎁터를 통해서 호출하게 될 텐데,
     * 프론트 컨트롤러는 (예를 들어) ControllerV3(핸들러 매핑 정보)를 가지고
     * 이 핸들러를 처리할 수 있는 핸들러 어뎁터를 조회해와서 이 어뎁터를 통해 (->handler() 호출) 핸들러를 호출하게 된다.

     * 어뎁터 주의?할 점
     * 어뎁터는 실제 컨트롤러를 호출하고 그 결과로 무조건 ModelView(데이터)를 반환해야 한다.
     * 실제 컨트롤러가 ModelView를 반환하지 못하는 컨트롤러면, 어뎁터가 ModelView를 직접 생성해서라도 반환해야 한다.
     */
    private Map<String, ControllerV4> controllerV4Map = new HashMap<>();

    public FrontControllerServletV4() {
        controllerV4Map.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerV4Map.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerV4Map.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        /**
         * v3과 다른 점 비교해보셈
         * URI에 해당하는 컨트롤러 찾음
         * Request의 데이터를 Map에 담음
         * controller로부터 데이터를 받기 위해 빈 Map을 생성한다. < 다름
         * controller가 빈 Map에 데이터를 담고, 논리주소를 반환한다. < 다름
         * 논리주소를 물리주소로 변환한다.
         * dispatcher을 불러서 jsp 랜더링 호출
         */

        ControllerV4 controller = controllerV4Map.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Map<String, String> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>(); // 추가
        String viewName = controller.process(paramMap, model);

        //논리이름 -> 물리이름
        MyView view = viewResolver(viewName);

        view.render(model, request, response);
    }

    private static MyView viewResolver(String viewName) {
        MyView view = new MyView("/WEB-INF/views/" + viewName + ".jsp");
        return view;
    }

    //paramMap을 넘겨야한다 -> request 다 꺼내서 paramMap에 담는다.
    private static Map<String, String> createParamMap(HttpServletRequest request) {

        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}