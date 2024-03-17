package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

	@Autowired
	CallService callService;

	@Test
	void printProxy() {
		log.info("callService class={}", callService.getClass()); //cglib
	}

	//트랜잭션 적용 o
	@Test
	void internalCall() {
		callService.internal();
	}

	//트랜잭션 적용 x -> 여기서의 callService 는 트랜잭션 프록시인데, external() 가 @Transactional 이 없기 때문에 aop (트랜잭션) 을 적용하지 않는다! -> 적용하지 않고 실제 callService 를 호출해버리는데,
	//이때 자바에서 메서드 앞에 별도의 참조가 없으면 this 로 인식을 하여, 다음 호출하는 internal() 은 실제 callService 의 internal() 로 인식을 하게 된다. -> 즉, @Transactional 이 없는 internal() 이 호출된다.
	@Test
	void externalCall() {
		callService.external();
	}

	//aop가 적용된 클래스와 메서드가 하나라도 있으면 동적 프록시가 생성되고 빈으로 등록된다.
	@TestConfiguration
	static class InternalCallV1TestConfig {
		@Bean
		CallService callService() {
			return new CallService();
		}
	}

	//서비스
	@Slf4j
	static class CallService {
		public void external() {
			log.info("call external");
			printTxInfo();
			internal();
		}
		//얘만 트랜잭션
		@Transactional
		public void internal() {
			log.info("call internal");
			printTxInfo();
		}

		private void printTxInfo() {
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("tx active={}", txActive);
			boolean readOnly = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("tx readOnly={}", readOnly);
		}
	}

}
