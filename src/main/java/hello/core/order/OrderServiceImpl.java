package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;

public class OrderServiceImpl implements OrderService {

//    private final MemberRepository memberRepository = new MemoryMemberRepository();
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy(); //SERVICE가 구체화에도 의존하고 있다(안다) (DIP 위반)
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); //변경 시 코드 고쳐야한다 (OCP 위반)
//위의 코드는 무엇이냐면 남배우역할(인터페이스)를 맡은 남배우(구현체)기 여배우역할(인터페이스)를 하는 여배우(구현체)를 직접 초빙하는 것과 같음
//단일 책임 위반까지 일어나는 셈 -> 따라서 관심사를 분리해야한다 -> 그러려면 기획자가 나타나야겠다

    //private DiscountPolicy discountPolicy; //이렇게 해야 인터페이스에만 의존하도록 한다(DIP지킴), 근데 값이 할당 안됐으니 널 포인트 뜸(아놔)
                                             //그럼 어떻게 DIP 지키냐 -> 누가 인터페이스에 구현 객체를 대신 생성해서 대신 주입해줘야한다

    //final 하면 무조건 생성자로 할당되어야한다
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
