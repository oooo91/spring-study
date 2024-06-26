package hello.login.web.session;

import static org.assertj.core.api.Assertions.*;

import hello.login.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SessionManagerTest {

  SessionManager sessionManager = new SessionManager();

  @Test
  void sessionTest() {

    //세션 생성
    //서블릿 컨테이너 환경이 아니므로 HttpServletResponse 사용 불가 -> 스프링이 Mock 제공
    MockHttpServletResponse response = new MockHttpServletResponse();
    Member member = new Member();
    sessionManager.createSession(member, response);

    //요청이 응답 쿠키 저장
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(response.getCookies());

    //세션 조회
    Object result = sessionManager.getSession(request);
    assertThat(result).isEqualTo(member);

    //세션 만료
    sessionManager.expire(request);
    Object expired = sessionManager.getSession(request);
    assertThat(expired).isNull();
  }

}