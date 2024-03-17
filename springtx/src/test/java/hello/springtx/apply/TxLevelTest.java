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
public class TxLevelTest {

	@Autowired LevelService service;

	@Test
	void orderTest() {
		service.write();
		service.read();
	}

	@TestConfiguration
	static class TxLevelTestConfig {
		@Bean
		LevelService levelService() {
			return new LevelService();
		}
	}

	@Slf4j
	@Transactional(readOnly = true)
	static class LevelService {

		//얘는 수정
		@Transactional(readOnly = false) //디폴트가 false 라서 (readonly = false) 안 적어도 됨
		public void write() {
			log.info("call write");
			printTxInfo();
		}

		//얘는 읽기
		public void read() {
			log.info("call read");
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
