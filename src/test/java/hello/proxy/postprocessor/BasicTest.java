package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BasicTest {

	@Test
	void basicConfig() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BasicConfig.class);

		//A 는 빈으로 등록된다.
		A a = applicationContext.getBean("beanA", A.class);
		a.helloA();

		//B 는 빈으로 등록되지 않는다.
		Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(B.class));
	}

	@Slf4j
	@Configuration
	static class BasicConfig {
		@Bean(name = "beanA")
		public A a() {
			return new A();
		}

		@Bean
		public AtoBPostProcessor postProcessor() {
			return new AtoBPostProcessor();
		}
	}

	@Slf4j
	static class A {
		public void helloA() {
			log.info("hello A");
		}
	}

	@Slf4j
	static class B {
		public void helloB() {
			log.info("hello B");
		}
	}

	@Slf4j
	static class AtoBPostProcessor implements BeanPostProcessor {

		//초기화 다 끝나고 실행
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
			log.info("beanName={}, bean={}", beanName, bean);
			if (bean instanceof A) { //스프링 컨테이너에 A 가 등록되면 B 로 바꿔치기 하자
				return new B();
			}
			return bean;
		}
	}

}
