package hello.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration 필요가 없는게 componentscan이 controller 찾음
public class HelloConfig {

    @Bean
    public HelloController helloController() {
        return new HelloController();
    }
}
