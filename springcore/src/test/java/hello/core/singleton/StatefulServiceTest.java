package hello.core.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        /* 문제 발생
        //ThreadA: A 사용자 10000원 주문
        statefulService1.order("userA", 10000);
        //ThreadB: B 사용자 20000원 주문 -> A가 주문하는 중에 B가 값을 바꿈
        statefulService2.order("userB", 20000);

        //ThreadA: A 사용자 주문 금액 조회
        int price = statefulService1.getPrice();
        System.out.println("price = " + price);

        //같은 인스턴스를 사용하기 때문에 값이 변함
        assertThat(statefulService1.getPrice()).isEqualTo(20000); */

        //ThreadA: A 사용자 10000원 주문
        int userAPrice = statefulService1.order("userA", 10000);
        //ThreadB: B 사용자 20000원 주문 -> A가 주문하는 중에 B가 값을 바꿈
        int userBPrice = statefulService2.order("userB", 20000);

        assertThat(userAPrice).isEqualTo(10000);
        assertThat(userBPrice).isEqualTo(20000);
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}