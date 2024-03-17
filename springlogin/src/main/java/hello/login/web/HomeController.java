package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentResolver.Login;
import hello.login.web.session.SessionManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    //Request header - SetCookie 값 -> @CookieValue 로 받는다, 로그인 안 한 사용자도 접속할 수 있어야하므로 required = false
    //@GetMapping("/")
    public String homeLogin(@CookieValue(name="memberId", required = false) Long memberId,
        Model model) {

        if (memberId == null) {
            return "home";
        }

        //로그인
        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) {
            return "home";
        }

        //성공 로직
        model.addAttribute("member", loginMember);
        return "loginHome"; //사용자 전용 화면
    }

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        //세션 관리자에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);

        //로그인
        if (member == null) {
            return "home";
        }

        //성공 로직
        model.addAttribute("member", member);
        return "loginHome"; //사용자 전용 화면
    }

    //@GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        //true 면 로그인 하지 않은 사용자도 세션이 생성되므로 세션을 false 로 받는다.
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 회원 데이터가 없다면 home
        if (loginMember == null) {
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome"; //사용자 전용 화면
    }

    //request 에서 getSession 해오는 거 번잡하다 -> paramter로 getSssion 바로 받을 수 있다.
    //스프링이 @SessionAttribute 제공한다. -> 이 기능은 세션을 생성하진 않으므로 조회할 때 사용하면 되겠다.
    //@GetMapping("/")
    public String homeLoginV4(
        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
        HttpServletRequest request, Model model) {

        //세션에 회원 데이터가 없다면 home
        if (loginMember == null) {
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome"; //사용자 전용 화면
    }

    //@Login 어노테이션이 있고, Member 를 파라미터로 받는다면 ModelAndAttribute 가 아니라 ArgumentResolver 가 동작되도록 ArgumentResolver 구현한다.
    //argumentResolver 를 통해 -> 바로 Member 로 받을 수 있게 한다.
    @GetMapping("/")
    public String homeLoginV5(@Login Member loginMember, Model model) {

        if (loginMember == null) {
            return "home";
        }
        model.addAttribute("member", loginMember);
        return "loginHome"; //사용자 전용 화면
    }
}