package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycleTest {
    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        //ApplicationContext -> close 제공 안해서 ConfigurableApplicationContext 로 생성
        ac.close(); //스프링 컨텍스트 닫기 (서버 닫기 예시)
    }

    //예를 들어 네트워크 연결이나 DB CONNECTION 연결 같은 것들은 app server가 뜨기 전에 미리 연결을 해둬야한다
    //또한 종료도 app server가 종료될 시점에 이러한 연결이 종료되어야한다
    //이러한 객체의 초기화/종료작업을 스프링이 제공해준다
    //ex) 서버 시작 시점에 네트워크 connect(), 종료 직전에 disconnect()

    //생성자 호출, url : null
    //connect : null
    //call : null message : 초기화 연결 메시지
    //당연한 얘기나 객체가 생성될 때 url 정보가 없기 때문에 null이 떨어진다. 객체를 생성한 다음 외부에서 수정자 주입을 통해 setUrl()이 호출되어야 비로소 url이 존재하게 된다.
    //스프링 빈은 다음과 같은 라이프 사이클을 가지는데 -> 객체를 생성한 후 의존관계를 주입한다. (생성자 주입은 논외, 얘는 바로 주입됨)
    //스프링 빈은 객체를 생성하고 의존 관계 주입이 끝난 다음에야 필요한 데이터를 사용할 수 있는 준비가 완료된다. (생성한 단계에는 값이 없으므로 함부로 연결을 할 순 없다)
    //따라서 초기화 작업은 의존관계 주입이 모두 완료된 다음에 (필요한 정보가 다 들어오고 난 후) 호출해야한다.
    //어떻게 의존관계 주입이 다 된 시점을 알 수 있을까? -> 스프링이 제공해준다.
    //또한 종료 단계에서도 스프링은 스프링 컨테이너가 종료되기 직전에 소멸 콜백을 준다.

    //스프링(단, 싱글톤)은 이벤트 라이프사이클을 가진다.
    //스프링 컨테이너 생성 -> 스프링 빈 생성 -> 의존관계 주입 -> '초기화 콜백' (니가 하고 싶은대로 하셈) -> application 동작 (사용) -> '소멸 전 콜백' -> 스프링 종료
    //초기화 콜백 : 빈이 생성되고 빈의 의존관계 주입이 완료된 후 호출
    //소멸 콜백 : 빈이 소멸되기 직전에 호출

    //최대한 생성자에서 끝내는 게 낫지 않냐? -> ㄴㄴ 객체의 생성(new)과 초기화를 분리하는 것이 좋다
    //단일책임원칙 지키자
    //생성자는 필수 정보를 받고 메모리를 할당해서 객체를 생성하는 책임을 진다
    //반면 초기화는 이렇게 생성된 값을 활용하여 외부 커넥션을 연결하는 무거운 동작을 수행한다
    //따라서 나누는 것이 유지보수 관점에서 좋다 (물론 초기화 작업이 작은 단위라면 생성자에서 한 번에 다 처리하는 게 더 나을 수 있다)

    //스프링은 크게 3가지 방법으로 빈 생명주기 콜백을 지원한다.
    //초기화, 소멸 전 콜백을 받는 방법 총 3가지
    //1. 인터페이스 InitializingBean, DisposableBean
    //단점 : 스프링 전용 메서드라 너무 스프링에 의존적이다. (코드 레벨로 가져오는 건 부담이란다) / 내가 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 없다.

    //2. 메서드 (initMethod = "init", destroyMethod = "close")
    //장점 : 메서드 이름 내 마음대로 / 코드가 아니라 설정 정보를 사용하기 때문에 코드를 고칠 수 없는 외부 라이브러리에도 초기화, 종료 메서드를 적용할 수 있다.
    //특징 : @Bean으로 등록될 때만 적용 가능하다. / destroyMethod 기본값이 close 또는 shutdown 이라는 이름의 종료 메서드를 찾아서 알아서 종료시킨다.

    //3. 어노테이션 (PostConstruct, PreDestroy)
    //최신 스프링에서 가장 권장하는 방법이다
    //어노테이션 하나만 붙이면 되므로 매우 편리하다.
    //javax.annotation.PostConstruct -> 스프링에 종속된 기술이 아니라 자바 표준이라서 스프링 아닌 컨테이너에도 동작한다.
    //단점 : 외부 라이브러리에 적용하지 못함 (수정을 못하니까), 그럴 때는 메서드 사용하기

    @Configuration
    static class LifeCycleConfig {
        @Bean(initMethod = "init", destroyMethod = "close")
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
}
