package hello.login.web.session;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 세션 종료 시점 : 세션 생성이 아니라 세션 요청 시점부터 ~ 몇 분으로 설정한다.
 세션 생성 시점이면 매번 ~ 몇 분마다 로그인을 시켜야하지만 세션 요청 시점부터는 해당 브라우저에서 어떤 동작을 한다면 그 동작 시점부터 30분 연장해주는 방식으로
 새로 로그인을 하지 않아도 되고, 사용자들은 보통 로그아웃을 클릭하지 않고 브라우저를 꺼버린다. 그래도 어떤 동작 이후부터 만료 시간이 설정되어있기 때문에
 메모리에 저장된 세션도 만료 시간 후 삭제되므로, 메모리의 효율도 지킬 수 있다.
 inactive 에서 설정한 시간동안 아무런 동작을 하지 않는다면 세션을 종료시키고, 동작을 한다면 lastAccessTime 이 + inactive 만큼 계속 연장될 것이다.
 */


@Slf4j
@RestController
public class SessionInfoController {

  @GetMapping("/session-info")
  public String sessionInfo(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    if (session == null) {
      return "세션이 없습니다.";
    }
    session.getAttributeNames().asIterator()
        .forEachRemaining(name -> log.info("session name={}, value={}", name, session.getAttribute(name)));

    log.info("sessionId={}", session.getId());
    log.info("getMaxInactiveInterval", session.getMaxInactiveInterval());
    log.info("creationTime={}", new Date(session.getCreationTime()));
    log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));
    log.info("isNew={}", session.isNew());

    return "세션 출력";
  }
}
