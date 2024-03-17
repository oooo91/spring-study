package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberSaveControllerV4 implements ControllerV4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {

        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        /**
         * v3 컨트롤러가 계속 ModelView(MVC에서는 ModelAndView) 매번 생성하는 거 뿍친다
         * 이를 개선한 v4 컨트롤러는 ModelView를 반환하지 않고 ViewName만 반환한다. 프론트 컨트롤러가 Model까지 만들 것이다.

         * ModelView 필요 없다.
         * ModelView mv = new ModelView("save-result"); //jsp 논리 주소
         * mv.getModel().put("member", member); //데이터
        */
        model.put("member", member); //데이터 담기만 하면 된다.
        return "save-result"; //논리 이름
    }
}
