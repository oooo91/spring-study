package hello.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * http://localhost:8080/test
 * build.gradle를 open as project로 열면 스프링과 독립적인 순수한 자바 프로젝트가 생긴다 (현 프로젝트)
 * 터미널에서 ./gradlew build 명령어를 통해 해당 폴더를 빌드시킬 수 있다.
 * 빌드가 된면 build 폴더의 libs 폴더에 war 압축파일이 생성된다.
 * 프로젝트의 build.gradle에 war 파일을 생성하는 플러그인 때문이다.
 * 이 war 압축파일을 풀면 index.html(내가 만든 html파일) 과 WEB-INF에 TestServlet.class(내가 만든 클래스 파일)이 컴파일된 채로 있다.
 * 이 war 파일을 tomcat의 webapp 폴더 안에 복사하여 ROOT로 이름을 고친 후 (선택사항) 톰캣을 재시작하면 8080 포트로 해당 html 화면을 볼 수 있다.
 * 매번 개발 후 빌드를 이런 방식으로 했었으나 인텔리제이 같은 IDE는 사용할 톰캣만 설정한다면 이를 자동화해준다.
 */
@WebServlet(urlPatterns = "/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("TestServlet.service");
        resp.getWriter().println("test");
    }
}
