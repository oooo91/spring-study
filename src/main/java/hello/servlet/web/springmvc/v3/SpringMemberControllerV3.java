package hello.servlet.web.springmvc.v3;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 유연한 컨트롤러
 */
@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    private MemberRepository memberRepository = hello.servlet.domain.member.MemberRepository.getInstance();

    /**
     * 1. ModelAndView 반환 안해도 된다.
     * v3 에서 v4로 개선된 것처럼 매번 ModelAndView를 반환하기 보다는 view name만 반환되도 그에 해당하는 뷰를 찾을 수 있도록 한다.
     */
    //@RequestMapping(value = "/new-form", method = RequestMethod.GET)
    @GetMapping("/new-form")
    public String newForm() {
        return "new-form";
    }

    /**
     *  HttpServletRequest, HttpServletResponse 도 받을 수 있고 파라미터를 직접 받을 수 있다.
     *  @RequestParam 사용한다.
     *  @RequestParam("username") 은 request.getParameter("username") 와 거의 같은 코드라 생각하면 된다.
     *  물론 GET 쿼리 파라미터, POST Form 방식을 모두 지원한다.
     */
    //@RequestMapping(value = "/save", method = RequestMethod.POST)
    @PostMapping("/save")
    public String memberSave(@RequestParam("username") String username,
                                   @RequestParam("age") int age,
                                   Model model) {

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);

        return "save-result";
    }

    //@RequestMapping(method = RequestMethod.GET)
    @GetMapping
    public String members(Model model) {

        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);

        return "members";
    }

}
