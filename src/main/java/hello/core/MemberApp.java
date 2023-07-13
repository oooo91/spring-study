package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {
    public static void main(String[] args) {
        //AppConfig appConfig = new AppConfig();
        //MemberService memberService = appConfig.memberService(); //new로 직접 생성하지 않고 appconfig를 통해 생성

        //ApplicationContext = 스프링 컨테이너가 빈을 관리하겠다
        //기본에는 개발자가 AppConfig를 직접 생성해서 DI했지만 이제 스프링 컨테이너를 통해 DI한다
        //스프링 컨테이너는 @Bean이라 적힌 메서드를 일단 모두 호출해서 -> 반환된 객체를 스프링 컨테이너에 모두 등록한다.
        //이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다

        //@Bean이 붙은 메서드명을 스프링 빈의 이름으로 사용함
        //이제 스프링 컨테이너에서 객체를 찾아야한다. getBean(이름, 타입)을 통해 찾을 수 있다.
        //이제 스프링 컨테이너에 객체를 빈으로 등록하고, 빈을 찾아서 사용하도록 변경되었다.
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService =
                applicationContext.getBean("memberService", MemberService.class); //기본적으로 메서드 이름으로 컨테이너에 이름이 등록됨

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("newMember = " + member.getName());
        System.out.println("findMember = " + findMember.getName());
    }
}
