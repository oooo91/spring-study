package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = hello.servlet.domain.member.MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {

        List<Member> members = memberRepository.findAll();

        /**
         * 근데 v3 컨트롤러가 계속 ModelView(MVC에서는 ModelAndView) 매번 생성하는 거 뿍친다
         * 이를 개선한 v4 컨트롤러는 ModelView를 반환하지 않고 ViewName만 반환한다. 프론트 컨트롤러가 Model까지 만들 것이다.
         */
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);

        return mv;
    }
}
