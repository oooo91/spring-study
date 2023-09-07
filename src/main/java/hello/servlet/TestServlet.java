package hello.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * http://localhost:8080/test
 * 전통적인 방식 -> 내가 직접 WAR를 빌드해야한다.
 * 과거에 자바로 웹 애플리케이션을 개발할 때는 먼저 서버에 톰캣 같은 WAS(웹 애플리케이션 서버)를 설치했다.
 * 그리고 WAS에서 동작하도록 서블릿 스펙에 맞추어 코드를 작성하고 WAR 형식으로 빌드해서 war 파일을 만들었다.
 * 이렇게 만들어진 war 파일을 WAS에 전달해서 배포하는 방식으로 전체 개발 주기가 동작했다.

 * 구체적인 설명...
 * build.gradle를 open as project로 열면 스프링과 독립적인 순수한 자바 프로젝트가 생긴다 (현 프로젝트)
 * 터미널에서 ./gradlew build 명령어를 통해 해당 폴더를 빌드시킬 수 있다.
 * 빌드가 된면 build 폴더의 libs 폴더에 war 압축파일이 생성된다.
 * 프로젝트의 build.gradle에 war 파일을 생성하는 플러그인 때문이다.
 * 이 war 압축파일을 풀면 index.html(내가 만든 html파일) 과 WEB-INF에 TestServlet.class(내가 만든 클래스 파일)이 컴파일된 채로 있다.
 * 이 war 파일을 tomcat의 webapp 폴더 안에 복사하여 ROOT로 이름을 고친 후 (선택사항) 톰캣을 재시작하면 8080 포트로 해당 html 화면을 볼 수 있다.
 * 매번 개발 후 빌드를 이런 방식으로 했었으나 인텔리제이 같은 IDE는 사용할 톰캣만 설정한다면 이를 자동화해준다.

 * 최근 방식 -> 스프링 부트가 알아서 WAR를 빌드한다.
 * 최근에는 스프링 부트가 내장 톰캣을 포함하고 있다.
 * 애플리케이션 코드 안에 톰캣 같은 WAS가 라이브러리로 내장되어 있다는 뜻이다.
 * 개발자는 코드를 작성하고 JAR로 빌드한 다음에 해당 JAR를 원하는 위치에서 실행하기만 하면 WAS도 함께 실행된다.
 * 쉽게 이야기해서 개발자는 main() 메서드만 실행하면 되고, WAS 설치나 IDE 같은 개발 환경에서 WAS와 연동하는 복잡한 일은 수행하지 않아도 된다.

 * 그런데 스프링 부트는 어떤 원리로 내장 톰캣을 사용해서 실행할 수 있는 것일까?
 * 지금부터 과거로 돌아가서 톰캣도 직접 설치하고, WAR도 빌드하는 전통적인 방식으로 개발을 진행해보자.
 * 그리고 어떤 불편한 문제들이 있어서 최근 방식으로 변화했는지 그 과정을 함께 알아보자.
 * 이미 잘 아는 옛날 개발자들은 WAS를 설치하고 war를 만들어서 배포하는 것이 익숙하겠지만,
 * 최근 개발자들은 이런 것을 경험할 일이 없다. 그래도 한번은 알아둘 가치가 있다.
 * 과거에 어떻게 했는지 알아야 현재의 방식이 왜 이렇게 사용되고 있는지 더 깊이있는 이해가 가능하다.

 * 서블릿 컨테이너도 설정하고, 스프링 컨테이너도 만들어서 등록하고,
 * 디스패처 서블릿을 만들어서 스프링 MVC와 연결하는 작업을 스프링 부트 없이 직접 경험해보자.
 * 그러면 스프링 부트가 웹 서버와 어떻게 연동되는지 자연스럽게 이해할 수 있을 것이다.
 * 참고로 여기서는 web.xml 대신에 자바 코드로 서블릿을 초기화 한다.
 * 옛날 개발자라도 대부분 web.xml을 사용했지 자바 코드로 서블릿 초기화를 해본 적은 없을 것이므로 꼭 한번 코드로 함께 따라해보자.
 */
@WebServlet(urlPatterns = "/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("TestServlet.service");
        resp.getWriter().println("test");
    }
}
