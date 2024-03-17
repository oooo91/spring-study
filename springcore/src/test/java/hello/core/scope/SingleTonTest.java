package hello.core.scope;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.assertj.core.api.Assertions.*;

public class SingleTonTest {

    @Test
    void singletonBeanFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class);
        SingletonBean singletonBean1 = ac.getBean(SingletonBean.class);
        SingletonBean singletonBean2 = ac.getBean(SingletonBean.class);

        System.out.println("singletonBean1 : " + singletonBean1);
        System.out.println("singletonBean2 : " + singletonBean2);

        //똑같은 인스턴스
        assertThat(singletonBean1).isSameAs(singletonBean2);
        ac.close();
    }

    @Scope("singleton") //디폴트라 생략 가능
    static class SingletonBean {
        @PostConstruct
        public void init() {
            System.out.println("SingletonBean.init");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("SingletonBean.destroy");
        }
    }
    //지금까지 스프링 컨테이너가 생성이 되면 빈이 생성되고, 컨테이너가 종료되면 빈이 종료되는 것으로 알고 있었는데 이게 가능했던 이유는
    //기본적으로 빈은 싱글톤 스코프로 생성이 되기 때문임
    //싱글톤 스코프 : 스프링 컨테이너와 생명주기를 함께 함 (가장 긴 범위임)
    //프로토타입 스코프 : 빈 생성 -> 의존관계 주입 -> postconstruct까지만 관여하고 이후 관여하지 않음
    //웹 스코프 : 스프링 + 웹 관련 기능이 있을 경우 이루어지는 스코프
    //1. request 스코프 : 응답이 이루어지기 전까지의 범위
    //2. session : 세션이 종료되기 전까지의 범위
    //3. application : 서블릿 컨텍스트와 함께하는 생명주기

    //싱글톤 스코프의 빈을 조회하면 -> 스프링 컨테이너는 항상 같은 인스턴스의 스프링 빈을 반환함
    //프로토타입 스코프를 조회하면 -> 스프링 컨테이너는 요청을 하면 그때서야 새로운 인스턴스를 생성해서 반환함, 항상 새로운 인스턴스를 반환함
    //인스턴스 반환 이후 관여, 관리하지 않음, 의존관계 주입하고 postcontruct까지 처리 후 관여하지 않음)
    //따라서 프로토타입은 predestroy 호출되지 않는다. (이게 중요)
    //따라서 프로토타입을 관리할 책임은 프로토타입을 받는 클라이언트에 있음
}
