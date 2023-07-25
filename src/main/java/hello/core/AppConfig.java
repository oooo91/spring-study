package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//구현 객체를 생성하고 연결해주는 전반적인 환경설정 클래스 (애플리케이션의 전체 동작 방식을 구성하기 위해)
@Configuration //<-구성정보를 담당한다는 뜻
public class AppConfig {

    //service단에서 new ~ 했던 걸 app config에서 다 해준다. (밖에서 생성해줘서 주입해준다)
    /*
        public MemberService memberService() {
            //AppConfig -> memoryMemberRepository 생성하고, memberServiceImpl을 생성하고 해당 repository를 주입하는 것까지 담당
            return new MemberServiceImpl(new MemoryMemberRepository());
        }

        public OrderService orderService() {
            return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
        }
    */

    //AppConfig보다 Spring Container가 더 나은 이유 중 하나
    //스프링은 빈을 생성하고 의존관계를 주입하는 단계가 나뉘어져 있는데 스프링 빈을 등록하게 되면 생성자를 호출하면서 의존관계 주입도 한 번에 처리됨
    //이를 의존 관계 자동 주입이라고 함

    //리팩토링 진행했음 -> 중복이 있고 역할에 따른 구현이 잘 안 보이는 걸 이리 바꿈
    //memberService는 key고 MemberServiceImpl이 value 라고 생각하면 됨
    @Bean //<-각 메서드에 빈으로 등록하면 스프링 컨테이너에 등록된다.
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    //서비스 바꾸고 싶으면 FixDiscountPolicy() 를 그냥 RateDiscountPolicy로 코드 하나만 갈아끼워주면 끝남
    //사용 영역(service)는 전혀 건들일 필요가 없다, 구성 영역(AppConfig)만 바꾸면 된다
    @Bean
    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

}
