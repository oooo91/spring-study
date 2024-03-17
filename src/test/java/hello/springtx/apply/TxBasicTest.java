package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class TxBasicTest {

	//빈 주입
	@Autowired BasicService basicService;

	//AOP 테스트
	@Test
	void proxyCheck() {
		log.info("aop class={}", basicService.getClass());
		Assertions.assertThat(AopUtils.isAopProxy(basicService)).isTrue(); //AOP 냐 아니냐 -> TRUE -> CGLIB 클래스가 나온다. (@Transactional 떄문에)
	}

	//트랜잭션 테스트
	@Test
	void txTest() {
		basicService.tx();
		basicService.nonTx();
	}

	//테스트 컨텍스트 빈 등록
	@TestConfiguration
	static class TxApplyBasicConfig {
		@Bean
		BasicService basicService() {
			return new BasicService();
		}
	}

	//트랜잭션 테스트를 위한 서비스
	@Slf4j
	static class BasicService {

		@Transactional //트랜잭션 적용 되는가
		public void tx() {
			log.info("call tx");
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive(); //트랜잭션 호출되는지 확인, 트랜잭션 들어온 스레드가 호출해줌
			log.info("tx active={}", txActive); //TRUE
		}

		public void nonTx() {
			log.info("call tx");
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive(); //트랜잭션 호출되는지 확인, 트랜잭션 들어온 스레드가 호출해줌
			log.info("tx active={}", txActive); //FALSE
		}
	}

}
