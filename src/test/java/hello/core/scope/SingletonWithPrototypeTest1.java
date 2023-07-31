package hello.core.scope;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonWithPrototypeTest1 {

    //프로토타입 스코프 - 싱글톤 빈과 함께 사용 시 문제점
    //프로토타입은 스프링 컨텍스트에 요청할 때마다 새로 생긴다.
    //프로토타입 빈 직접 요청 -> count = 1;
    //다른 클라이언트가 프로토타입 빈 요청 -> count = 1;

    //그런데 싱글톤과 프로토타입을 같이 쓰면 문제점이 발생한다.
    //예를 들어 싱글톤에서 프로토타입인 빈을 2개 주입 받았다고 가정하자.
    //클라이언트가 싱글톤을 요청하면, 일반적으로 생각했을 때 가진 2개의 프로토타입 객체가 각각 count + 1이 될 거라 생각할 것이다.
    //그러나 count = 2가 됐다. 그 이유는 싱글톤에서 한 번 주입된 프로토타입은 싱글톤처럼 계속 쓰여지기 때문이다.
    //즉 사용 시점마다 새로 생성되는 것이 아니기 때문에 처음 주입된 프로토타입을 계속 사용하여 count가 2가 된 것이다.

    //다시 말해 왜 2가 됐냐면
    //생성자 주입 시 프로토타입이 주입돼서 계속 같은 걸 쓰게 된다.
    //원래 스프링 컨테이너에서 요청 받을 때마다 생성되는건데, 처음 주입 받을 때만 딱 들어와버리는 것이다. (싱글톤이니까)
    //싱글톤 빈이 생성 시점에만 의존관계를 주입받기 때문에 프로토타입 빈이 생성되긴 하는데 싱글톤 빈과 함꼐 유지되는 문제인 것이다.
    //요청마다 새로 생성해서 쓰고 싶다면? -> 싱글톤 객체 두 개 만들어서 (각각 프로토타입 객체를 하나씩 가지고 있다) 스프링에 요청하면,
    //요청할 때 새로 생긴다고 했으니까 다른 프로토타입이 각각 들어오겠네

    //근데 이처럼 스프링 컨텍스트로부터 주입을 받는 방식은 너무 스프링 컨텍스트에 종속적인 코드가 될 것이고 단위 테스트도 어려워진다.
    //DL이란, Dependency Lookup으로 Dependency Injection과 다르게 주입 받는 형식이 아니라 조회(탐색)하는 것을 말한다.
    //다시 말해 사용할 때마다 새로 프로토타입을 생성해서 쓰기 위해 주입이 아닌 탐색 정도 되는 기능만 있으면 좋을 것 같다. -> ObjectProvider
    //단, ObjectProvider 프로토타입 전용으로 쓰이는 게 아니라 대신 조회해주는 기능 정도로 이해하면 된다.
    //근데 이제 프로토타입이다보니 요청되면 새로 생성해주기 때문에 ObjectProvider.getObject()를 하게 되면 새롭게 객체를 생성해주는 것이다.

    //단, ObjectProvider -> 스프링에서 제공해주는 DL 기능으로 스프링에 종속적이다.
    //javax.~~ 시작되는 것은 자바 표준을 사용하는 방법인데, implementation 'javax.inject:javax.inject:1'를 사용하면 스프링에 종속적이지 않는 DL 기능을 사용할 수 있다.
    //단점이 이 라이브러리를 gradle에 추가해야됨

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCount();
        assertThat(prototypeBean1.getCount()).isEqualTo(1);

        PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCount();
        assertThat(prototypeBean2.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        //assertThat(count2).isEqualTo(2); //싱글톤에서 생성자 주입으로 프로토타입을 받아왔을 때
        assertThat(count2).isEqualTo(1); //provider로 해당 프로토타입을 탐색하여 받아왔을 때
    }

    @Scope("singleton")
    @RequiredArgsConstructor
    static class ClientBean {
        //private final PrototypeBean prototypeBean; //생성시점에 주입
        //private final ObjectProvider<PrototypeBean> prototypeBeanProvider; //provider가 스프링에서 ProtoTypeBean이 나옴, DL기능제공
        private final Provider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
            //prototypeBean.addCount();
            //return prototypeBean.getCount();
            //PrototypeBean prototypeBean = prototypeBeanProvider.getObject(); //스프링 provider 사용
            PrototypeBean prototypeBean = prototypeBeanProvider.get();//자바 표준 provider 사용
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }

}
