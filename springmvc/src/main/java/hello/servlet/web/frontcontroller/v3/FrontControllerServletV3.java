package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Model 추가를 하면? -> 서블릿 종속성 제거, 뷰 이름 중복 제거
 * 1. 서블릿 종속성 제거
 * 컨트롤러 입장에서 HttpServletRequest, HttpServletResponse 꼭 필요가 없다.
 * request 없이도 데이터를 받아보고, request.setAttribute() 사용하지 않고도 충분히 jsp 에 데이터를 반환할 수 있다.
 * 자바의 Map을 데이터를 대신 넘기도록 하면 컨트롤러는 서블릿 기술을 몰라도 충분히 동작할 수 있다.
 * 그리고 request 객체 대신 별도의 Model 객체를 만들어서 데이터를 반환할 수 있다.
 * 이렇게 작성을 하면 구현 코드는 더 단순해지고 테스트 코드 작성이 쉬워진다.

 * 2. 뷰 이름 중복 제거
 * 이전 컨트롤러에서 지정하는 뷰 이름들을 보면 중복이 있다. (실제 물리 위치라서)
 * 이러한 방법보단 컨트롤러는 '뷰의 논리 이름'을 반환하고, 실제 물리 위치는 프론트 컨트롤러(->스프링MVC에서는 뷰 리졸버를 통해 찾음)에서 처리하도록 한다.
 * 이렇게 되면 컨트롤러는 request.getRequestDispatcher(실제 물리 위치) 를 사용하지 않고도 처리가 되며
 * 향후 뷰의 폴더 위치가 이동하더라도 프론트 컨트롤러만 수정하면 된다.
 * 즉 웬만한 지저분한 코드는 프론트 컨트롤러가 하도록 한다... (여태 그걸 배움)
 */
@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerV3Map = new HashMap<>();

    public FrontControllerServletV3() {
        controllerV3Map.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerV3Map.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerV3Map.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        /**
         * URI에 해당하는 컨트롤러 찾음
         * Request의 데이터를 Map에 담음
         * Controller을 통해 반환할 논리주소와 데이터를 ModelView로 반환받음
         * ModelView에서 논리 주소를 꺼내 물리 주소로 변환
         * dispatcher을 불러서 jsp 랜더링 호출
         */
        ControllerV3 controller = controllerV3Map.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controller.process(paramMap);

        //논리이름 -> 물리이름
        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);

        /**
        model(데이터)를 다 request에 담는다. (이렇게 하는 이유 : jsp에서 데이터 꺼내려면 request.setAttribute()가 필요하기 때문이다)
        다른 템플릿 쓸 경우 이런 식으로 데이터 처리 안해도 된다.
         */
        view.render(mv.getModel(), request, response);
    }

    private static MyView viewResolver(String viewName) {
        MyView view = new MyView("/WEB-INF/views/" + viewName + ".jsp");
        return view;
    }

    //paramMap을 넘겨야한다 -> request에서 데이터 다 꺼내서 paramMap에 담는다.
    private static Map<String, String> createParamMap(HttpServletRequest request) {

        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}