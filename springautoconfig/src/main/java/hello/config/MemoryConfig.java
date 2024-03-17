package hello.config;

import memory.MemoryCondition;
import memory.MemoryController;
import memory.MemoryFinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 3. 수동 조건 걸기
 * MemoryCondition 클래스에서 조건을 만들었다.
 * false를 반환하면 빈 등록x, true 반환하면 빈 등록o
 * (Edit configurtion에서 addVM 옵션에서 -Dmemory=on 추가하면 true 를 반환한다.)

 * 4. 자동 조건 걸기
 * 사실 Conditional 관련해서 스프링부트가 이미 만든 것도 있다 -> 굳이 조건 클래스 안 만들어도 된다 -> @ConditionalOnProperty
 * ConditionalOnProperty 얘도 결국 Condition을 구현하여 추상화하였다.
 * 이외에도 스프링은 @Conditional 과 관련해서 개발자가 편리하게 사용할 수 있도록 수 많은 @ConditionalOnXxx 를 제공한다.
 */
@Configuration
//@Conditional(MemoryCondition.class)
@ConditionalOnProperty(name="memory", havingValue = "on") //추가
public class MemoryConfig {

    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder());
    }

    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
}
