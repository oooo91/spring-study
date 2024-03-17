package hello.servlet.web.servlemvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller 역할
 * 그냥 jsp 로 가는 애
 */
@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
                                                    throws ServletException, IOException {
        //파일 경로로 jsp 를 직접 뿌리는 방법 말고 (외부에서 접근하는 방법),
        // 컨트롤러 거쳐서 jsp 뿌리려면 -> WEB-INF 폴더 안에 해당 jsp 파일 넣어주면 된다.
        String viewPath = "/WEB-INF/views/new-form.jsp";

        //controller 에서 view로 이동할 때 dispatcher 사용 (하나의 서블릿-다른 서블릿 혹은 서블릿-jsp와 연동/데이터 전달하는 방법)
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);

        //다른 서블릿이나 jsp로 이동할 수 있는 기능이다. 서버 내부에서 다시 호출이 발생한다. (클라이언트 전혀 인지 x)
        //redirect 처럼 클라이언트에 다시 갔다 오는 개념 x
        dispatcher.forward(request, response);
    }
}
