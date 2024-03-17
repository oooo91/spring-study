package hello.login.web.session;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * 세션 관리
 */
@Component
public class SessionManager {

  public static final String SESSION_COOKIE_NAME = "mySessionId";
  //세션 저장소 (ConcurrentHashMap -> 동시성)
  private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

  /**
   * 세션 생성
   * sessionId 생성 (임의의 추정 불가능한 랜덤 값)
   * 세션 저장소에 sessionId와 보관할 값 저장
   * sessionId로 응답 쿠키를 생성해서 클라이언트에 전달
   */
  public void createSession(Object value, HttpServletResponse response) {

    //세션 id 생성하고 값을 세션에 저장
    String sessionId = UUID.randomUUID().toString();
    sessionStore.put(sessionId, value);

    //쿠키 생성 (상수 생성 단축키 -> ctrl + alt + c)
    Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
    response.addCookie(mySessionCookie);
  }

  /**
   * 세션 조회
   */
  public Object getSession(HttpServletRequest request) {
    Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
    if (sessionCookie == null) {
      return null;
    }
    return sessionStore.get(sessionCookie.getValue()); //uuid
  }

  /**
   * 세션 만료
   */
  public void expire(HttpServletRequest request) {
    Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
    if (sessionCookie != null) {
      sessionStore.remove(sessionCookie.getValue()); //세션 저장소의 한 줄 지우기
    }
  }

  public Cookie findCookie(HttpServletRequest request, String cookieName) {
    //코드 합치는 단축키 -> ctrl + alt + n
    if (request.getCookies() == null) {
      return null;
    }
    //array 를 stream 으로 전환하여 stream 기능을 쓸 수 있게 한다, stream 은 각 노드의 값에 관련하여 처리한다.
    //findFirst 는 순차적인 조회 중 가장 먼저 반환되는 것을 return 하고 findAny 는 병렬 조회 중 먼저 나오는 것을 반환한다. 사실 여기서는 아무거나 써도 된다.
    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(cookieName))
        .findAny()
        .orElse(null);
  }
}
