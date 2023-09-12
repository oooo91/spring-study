package hello.selector;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 자동 구성 이해 - ImportSelector
 * @Import 에 설정 정보를 추가하는 방법은 2가지가 있다.
 * 정적인 방법: @Import (클래스) 이것은 정적이다. 코드에 대상이 딱 박혀 있다.
 * 설정으로 사용할 대상을 동적으로 변경할 수 없다.
 * 동적인 방법: @Import ( ImportSelector ) 코드로 프로그래밍해서 설정으로 사용할 대상을 동적으로 선택할 수 있다.

 * 정적인 방법
 * 스프링에서 다른 설정 정보를 추가하고 싶으면 다음과 같이 @Import 를 사용하면 된다.
 * @Configuration
 * @Import({AConfig.class, BConfig.class}) --> A도, B도 Config로 사용한다는 뜻
 * public class AppConfig {...}
 * 그런데 예제처럼 AConfig , BConfig 가 코드에 딱 정해진 것이 아니라, 특정 조건에 따라서 설정 정보를 선택해야 하는 경우에는 어떻게 해야할까?

 * 동적인 방법
 * 스프링은 설정 정보 대상을 동적으로 선택할 수 있는 ImportSelector 인터페이스를 제공한다.
 * 설정 정보를 동적으로 선택할 수 있게 해주는 ImportSelector 인터페이스를 구현했다.
 * 여기서는 단순히 hello.selector.HelloConfig 설정 정보를 반환한다.
 * 이렇게 반환된 설정 정보는 선택되어서 사용된다.
 * 여기에 설정 정보로 사용할 클래스를 동적으로 프로그래밍 하면 된다.
 */
public class ImportSelectorTest {

    @Test
    void staticConfig() {
        AnnotationConfigApplicationContext applicationContext
                = new AnnotationConfigApplicationContext(StaticConfig.class);
        HelloBean bean = applicationContext.getBean(HelloBean.class);
        Assertions.assertThat(bean).isNotNull();
    }

    @Test
    void selectorConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                SelectorConfig.class);
        HelloBean bean = applicationContext.getBean(HelloBean.class);
        Assertions.assertThat(bean).isNotNull();
    }

    /**
     * 동적인 방식
     * HelloImportSelector를 실행한 결과값을 반환한다 -> 동적으로 선택된다.
     */
    @Configuration
    @Import(HelloImportSelector.class)
    public static class SelectorConfig {

    }

    /**
     * 정적인 방식
     */
    @Configuration
    @Import(HelloConfig.class)
    public static class StaticConfig {

    }
}
