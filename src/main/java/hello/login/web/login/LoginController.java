package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 쿠키 -> 브라우저에서 값 변경 가능, 탈취 등 (클라이언트는 보안 취약하다)
 대안 -> 추정 가능한 값을 브라우저에 저장하면 안 된다
 예측이 불가능한 임의의 값 (sessionId(토큰), 서블릿이 지원하는 http session 은 JSESSIONID 이름으로 보낸다) 을 쿠키에 저장하고, 세션 저장소에서 토큰과 매핑된 정보를 찾는 방향으로 개발해야한다.
 근데 토큰도 -> 정보와 매핑되어있기 때문에 쿠키처럼 토큰 탈취하면 얼마든지 사용자 정보 사용할 수 있다.
 이 대안은 -> 토큰의 만료 시간을 짧게 구현하여 가령 만료 시간 : 5분이라고 했을 떄 5분 뒤는 탈취된 해당 토큰을 사용할 수 없도록 한다. -> 세션을 사용한다.

 세션을 사용해서 해결할 수 있는 보안 문제
 쿠키 값 변조 -> 예측 불가능한 세션 id 를 사용
 탈취 문제 -> 토큰을 쿠키로 반환하므로 중요한 정보 탈취 할 수 없다
 토큰 탈취 문제 -> 시간이 지나면 사용 못하도록 세션 만료 시간을 짧게 설정한다, 또는 세션 저장소 한 줄 삭제하면 그만이다
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

  private final LoginService loginService;
  private final SessionManager sessionManager;

  @GetMapping("/login")
  public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
    return "login/loginForm";
  }

  //로그인
  //@PostMapping("/login")
  public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
      HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      return "/login/loginForm";
    }
    Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

    //global error -> 객체에 대한 검증은, 이처럼 db 에서 조회하기까지하는 검증은 @ScriptAssert 로 검증하기에 다소 무리가 있다.
    //객체에 대한 검증은 직접 코드로 작성하는 것이 좋다.
    if (loginMember == null) {
      bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
      return "/login/loginForm";
    }

    //login 성공 처리

    //쿠키에 시간 정보를 주지 않으면 세션 쿠키 (기본값, 브라우저 종료 시 쿠키 만료)
    //Response header 에 SetCookie 저장되어있다. -> 앞으로 요청 시 Request header 에 쿠키가 넘어간다.
    Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
    response.addCookie(idCookie);

    return "redirect:/";
  }

  //@PostMapping("/login")
  public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
      HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      return "/login/loginForm";
    }
    Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

    if (loginMember == null) {
      bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
      return "/login/loginForm";
    }

    //login 성공 처리
    //세션 관리자를 통해 세션을 생성하고 회원 데이터를 보관한다.
    sessionManager.createSession(loginMember, response);

    return "redirect:/";
  }

  //서블릿이 제공하는 HttpSession -> session 다루기
  //@PostMapping("/login")
  public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
      HttpServletRequest request) {

    if (bindingResult.hasErrors()) {
      return "/login/loginForm";
    }
    Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

    if (loginMember == null) {
      bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
      return "/login/loginForm";
    }

    //login 성공 처리
    //세션이 있으면 세션 반환하고, 없으면 신규 세션을 생성한다. = true , 생성하지 않으면 = false
    HttpSession session = request.getSession(true); //true 기본값이라 생략 가능
    //세션에 로그인 회원 정보 보관
    session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

    return "redirect:/";
  }

    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
        @RequestParam(defaultValue = "/") String redirectURL, //추가, redirectURL 없으면 /
        HttpServletRequest request) {

      if (bindingResult.hasErrors()) {
        return "/login/loginForm";
      }
      Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

      if (loginMember == null) {
        bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
        return "/login/loginForm";
      }

      //login 성공 처리
      //세션이 있으면 세션 반환하고, 없으면 신규 세션을 생성한다. = true , 생성하지 않으면 = false
      HttpSession session = request.getSession(true); //true 기본값이라 생략 가능
      //세션에 로그인 회원 정보 보관
      session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

      return "redirect:" + redirectURL;
    }
  //@PostMapping("/logout")
  public String logout(HttpServletResponse response) {
    //쿠키를 삭제하는 방법 -> 시간 삭제
    expireCookie(response, "memberId");
    return "redirect:/";
  }

  //@PostMapping("/logout")
  public String logoutV2(HttpServletRequest request) {
    sessionManager.expire(request);
    return "redirect:/";
  }

  @PostMapping("/logout")
  public String logoutV3(HttpServletRequest request) {
    //false 로 반환해야한다, true 면 세션을 생성하기 때문에
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate(); //세션 삭제
    }
    return "redirect:/";
  }

  //함수 생성 -> ctrl + alt + m , 파라미터 뺴기 -> ctrl + alt + p
  private static void expireCookie(HttpServletResponse response, String cookieName) {
    Cookie cookie = new Cookie(cookieName, null);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

}
