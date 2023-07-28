package hello.core.discount;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Qualifier("mainDiscountPolicy") //컴파일 타입 때도 오류 잡을 수 있는 방법 -> @MainDiscountPolicy 수동으로 만든 어노테이션 붙이셈
@Component
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy {

    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100 ;
        } else {
            return 0;
        }
    }
}
