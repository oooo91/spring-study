package hello.core.autowired;

import hello.core.AutoAppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class AllBeanTest {

    @Test
    void findAllBean() {
        ApplicationContext ac =
                new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class); //빈 등록

        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);
        int discountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(discountPrice).isEqualTo(1000);

        int rateDiscountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");
        assertThat(rateDiscountPrice).isEqualTo(2000);
    }

    //DiscountService는 위 코드와 같이 직접 빈이 등록되고
    //생성자로 주입받으려니까 AutoAppConfig를 빈 등록하여 @Component ixDiscountPolicy, rateDiscountPolicy가 주입되도록 함
    //그리고 @Component로 등록되는 빈 이름은 클래스명을 사용한다 (앞글자는 소문자로)
    //따라서 discount() 함수를 호출할 때 빈 이름을 파라미터로 넣어주면 해당 클래스를 통하여 결과값이 나타난다
    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap; //map으로 모든 빈 받기
        private final List<DiscountPolicy> policies; //ㅣlist로 모든 빈 받기

        //이런 Map이나 List로 빈을 받을 때는 한 번에 타입을 구분할 수 없기 때문에 이렇게 자동보다는 @Bean을 이용한 수동으로 등록한다
        @Autowired
        public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            System.out.println("policyMap : " + policyMap);
            System.out.println("policies : " + policies);
        }

        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            return discountPolicy.discount(member, price);
        }
    }


}
