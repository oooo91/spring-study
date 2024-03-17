package hello.core.autowired;

import hello.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

public class AutowiredTest {

    //옵션처리 -> 스프링 빈이 없어도 동작해야할 때가 있다
    //이때 기본적으로 Autowired required true라서 자동 주입 대상이 없으면 오류가 발생하는데 이때 3가지 방법이 있다
    //1. Autowired required false -> 자동 주입 대상이 없으면 수정 메서드 아예 호출 못함
    //2. @Nullable -> 자동 주입할 대상 없으면 null 입력
    //3. Optional 사용 -> 대상 없으면 Optional.empty가 반환됨
    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class); //스프링 빈으로 등록
    }

    //@Component -> 빈으로 등록 안했음
    static class TestBean {

        @Autowired(required = false)
        public void setNoBean1(Member member1) {
            System.out.println("noBean = " + member1); //호출 자체가 안됨
        }

        @Autowired
        public void setNoBean2(@Nullable Member member2) {
            System.out.println("noBean = " + member2); //null 출력
        }

        @Autowired
        public void setNoBean3(Optional<Member> member3) {
            System.out.println("noBean = " + member3); //Optional.empty 출력
        }
    }

}













