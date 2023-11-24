package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

  //인터셉터의 장점 (필터보다 간편 기능 많음) -> excludePathPatterns 메소드를 제공하기 때문에 필터와 달리 화이트 리스트 없어도 제외할 대상을 쉽게 설정할 수 있다.
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    String requestURI = request.getRequestURI();
    log.info("인증 체크 인터셉터 실행 {}", requestURI);

    HttpSession session = request.getSession();

    if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
      log.info("미인증 사용자 요청");

      //로그인으로 redirect
      response.sendRedirect("/login?redirectURL=" + requestURI);
      return false;

    }
    return true;
  }
}
