package hello.core.singleton;

import hello.core.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConfigurationTest {

    @Test
    void configurationDeep() {
        //AppConfig도 컨테이너에 넘기면 스프링 빈으로 등록됨
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class); //AppConfig로 조회 가능 이유 -> AppConfig@CGLIP가 AppConfig 상속받음

        //순수한 클래스였다면 class hello.core.AppConfig가 출력되어야할 텐데 복잡한 이름의 클래스가 출력됨
        //스프링 빈으로 등록하는 과정에서 스프링이 CGLIP라는 바이트 코드를 조작하는 라이브러리를 사용하여
        //AppConfig를 상속받은 다른 클래스를 만들어서 -> AppConfig@CGLIB가 컨테이너에 등록됨
        //(AppConfig@CGLIB 예상 코드 -> memberRepository가 없으면 new 하고 등록 후 반환하고, 컨테이너에 이미 있으면 컨테이너에 있는 걸 반환하는 코드가 있을 것이다)
        //@Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면 걜 반환하고 없으면 생성해서 등록 후 반환하는 코드가 동적으로 만들어짐 -> 덕분에 싱글톤이 보장됨
        System.out.println("bean = " + bean.getClass());

    }
}
