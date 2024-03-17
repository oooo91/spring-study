package hello.springtx.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InitTxTest {

	@Autowired Hello hello;

	@Test
	void go() {
		//초기화 코드는 스프링이 초기화 시점에 호출한다.
		//hello.initV1() 이 호출된다.
	}

	@TestConfiguration
	static class InitTxTestConfig {
		@Bean
		Hello hello() {
			return new Hello();
		}
	}

	@Slf4j
	static class Hello {

		@PostConstruct
		@Transactional
		public void initV1() {
			boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("Hello init @PostConstructor tx active={}", isActive); //false 가 나온다... -> 초기화 코드가 호출되고, AOP 가 호출된다.
		}

		//따라서 스프링 빈이 다 만들어지고, AOP 까지 다 적용을 헀을 때, 초기화 시점에 호출하도록 이벤트를 적용한다.
		@EventListener(ApplicationReadyEvent.class) //지존 준비 완.
		@Transactional
		public void initV2() {
			boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("Hello init ApplicationReadyEvent tx active={}", isActive); //true 가 나온다.
		}
	}
}
