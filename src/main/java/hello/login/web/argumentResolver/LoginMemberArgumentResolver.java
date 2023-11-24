package hello.login.web.argumentResolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

  //@Login 어노테이션이 있고, Member 를 파라미터로 받는다면 ModelAndAttribute 가 아니라 ArgumentResolver 가 동작되도록 ArgumentResolver 구현한다.
  //supportsParameter 이 true -> resolveArgument 실행, false -> 실행 x
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    log.info("supportsParameter 실행");

    boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
    boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

    return hasMemberType && hasLoginAnnotation;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    log.info("resolveArgument 실행");

    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(); //HttpServletRequest 뽑는 방법
    HttpSession session = request.getSession(false);
    if (session == null) {
      return null;
    }
    return session.getAttribute(SessionConst.LOGIN_MEMBER);
  }
}
