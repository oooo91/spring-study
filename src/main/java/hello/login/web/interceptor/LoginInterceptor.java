package hello.login.web.interceptor;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

  public static final String LOG_ID = "logId";

  //default interface override 단축키 -> ctrl + o
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    
    String requestURI = request.getRequestURI();
    String uuid = UUID.randomUUID().toString();
    
    request.setAttribute(LOG_ID, uuid); //interceptor -> 싱글톤처럼 사용되므로 멤버변수 사용 x (중간에 다른 요청으로 값 변경됨)

    //handler 가 Object 가 된 이유는 handler 는 유연하게 아무거나 받고 핸들러 어댑터가 처리하기 때문이다.
    //handlerMethod 는 @RequestMapping 이 붙은 메소드의 정보를 추상화한 객체다.
    //handlerMethod 는 그 자체가 실행 가능한 객체가 아니라 메소드를 실행하기 위해 필요한 참조 정보를 담고 있는 객체로 다음과 같은 정보를 가진다.
    //빈 객체, 메소드 메타 정보, 메소드 파라미터 메타정보, 메소드 어노테이션 메타정보, 메소드 리턴 값 메타정보
    //디스패처 서블릿은 어플리케이션이 실행될 때 모든 컨트롤러 빈의 메소드를 살펴서 매핑 후보가 되는 메소드들을 추출한 뒤
    //이를 handlerMethod 형태로 저장해둔다. 그리고 실제 요청이 들어오면 저장해둔 목록에서 요청 조건에 맞는 handlerMethod 를 참조해서 매핑되는 메소드를 실행한다.

    //@RequestMapping 사용할 경우 -> HandlerMethod 반환
    //정적 리소스 : ResourceHttpRequestHandler 반환
    if (handler instanceof HandlerMethod) {
      HandlerMethod hm = (HandlerMethod) handler;
    }

    log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
    return true; //handler 실제 호출
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    log.info("postHandler [{}]", modelAndView); //postHandler -> modelAndView 반환, 예외 시 호출 x
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {

    String requestURI = request.getRequestURI();
    String logId = (String) request.getAttribute(LOG_ID);

    log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler); //response 반환
    if (ex != null) {
      log.error("afterCompletion error!", ex); //예외 발생하더라도 호출
    }
  }
}


