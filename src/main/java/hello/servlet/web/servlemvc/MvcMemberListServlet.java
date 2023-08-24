package hello.servlet.web.servlemvc;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "mvcMemberListServlet", urlPatterns = "/servlet-mvc/members")
public class MvcMemberListServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        /**
         * MVC 컨트롤러 한계점
         * 1. 포워드 중복
         * 2. VIEWPATH 중복
         * 3. 사용하지 않는 코드 -> 예를 들어 HttpServletRequest, HttpServletResponse만 봐도 사용할 때도 있고 사용하지 않을 때도 있다.
         * HttpServletRequest, HttpServletResponse 를 사용하는 코드는 테스트 케이스를 작성하기도 어렵다.
         * 4. 공통처리가 어렵다
         * -> 프론트 컨트롤러 패턴 필요 -> 스프링 MVC 핵심  (여태 같은 서블릿 객체 계속 생성했는데,
         * 이제 서블릿(front controller) 하나만 두고 얘가 클라이언트의 요청을 받음 -> 공통 처리 + 요청에 맞는 컨트롤러 찾아서 호출하도록 한다.
         * 그래서 프론트 컨트롤러를 제외한 나머지 컨트롤러는 서블릿을 사용하지 않아도 된다.
         * 스프링 웹 MVC의 DispatcherServlet도 프론트 컨트롤러 패턴으로 구현되어있다.
         */
        request.setAttribute("members", members);

        String viewPath = "/WEB-INF/views/members.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);

    }
}
