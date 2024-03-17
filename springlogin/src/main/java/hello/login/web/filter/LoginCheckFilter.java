package hello.login.web.filter;

import hello.login.web.SessionConst;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

@Slf4j
public class LoginCheckFilter implements Filter {

  //로그인하지 않아도 접근 가능한 url
  private static final String[] whiteList={"/", "/members/add", "/login", "/logout", "/css/*"};

  //default -> interface 무조건 구현 안 해도 됨
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestURI = httpRequest.getRequestURI();

    HttpServletResponse httpResponse = (HttpServletResponse) response;
    try {
      log.info("인증 체크 필터 시작 {}", requestURI);

      if (isLoginCheckPath(requestURI)) { //화이트 리스트에 포함되지 않은 requestURI
        log.info("인증 체크 로직 실행 {}", requestURI);
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
          log.info("미인증 사용자 요청 {}", requestURI);

          //로그인 안 해도 /items 로 막 이동됐던 게 막힌다. /items 로 이동 시 /login?redirectURL="/items" 으로 이동한다.
          //login 컨트롤러에서 redirectURL 이 있을 경우 로그인 성공 시 redirectURL 로 이동하도록 하자.
          httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
          return; //다음 서블릿이나 컨트롤러 호출하지 않겠다. (중요)
        }
      }
      chain.doFilter(request, response);
    } catch (Exception e) {
      throw e; //예외 로깅 가능하지만, 여기서 예외처리하면 정상 로직으로 처리되므로 톰캣까지 예외를 보내줘야한다.
    } finally {
      log.info("인증 체크 필터 종료 {} ", requestURI);
    }
  }

  /**
   * 화이트 리스트인 경우 인증 체크 x
   */
  private boolean isLoginCheckPath(String requestURI) {
    return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
  }
}
