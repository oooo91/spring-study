package hello.servlet.web.frontcontroller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 컨트롤러에서 뷰를 직접 반환하는 것이 아니라,
 * 프론트 컨트롤러로 뷰(MyView)를 반환하면 프론트 컨트롤러가 대신 render()를 호출하여 해당 뷰를 호출할 수 있도록 한다. (jsp forward)
 */
public class MyView {
    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    //뷰 랜더링 동작하도록 (그간의 컨트롤러마다 중복된 코드를 여기에 (프론트 컨트롤러는 공통 업무를 처리한다.))
    public void render(HttpServletRequest request, HttpServletResponse response)
                                            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
