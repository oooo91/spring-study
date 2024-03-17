package hello.springtx.exception;

import hello.springtx.exception.RollbackTest.RollbackService.MyException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

	@Autowired
	RollbackService service;

	//롤백됨
	@Test
	void runtimeException() {
		Assertions.assertThatThrownBy(() -> service.runtimeException()).isInstanceOf(RuntimeException.class);
	}

	//커밋됨
	@Test
	void checkedException() {
		Assertions.assertThatThrownBy(() -> service.checkedException()).isInstanceOf(MyException.class);
	}

	@TestConfiguration
	static class RollbackTestConfig {
		@Bean
		RollbackService rollbackService() {
			return new RollbackService();
		}
	}

	@Slf4j
	static class RollbackService {

		//런타임 예외 발생 : 콜백
		@Transactional
		public void runtimeException() {
			log.info("call runtimeException");
			throw new RuntimeException();
		}

		//체크 예외 발생 : 커밋
		//체크 예외라 잡거나 던져야 한단다
		@Transactional
		public void checkedException() throws MyException {
			log.info("call checkedException");
			throw new MyException();
		}

		//체크 예외 rollbackFor 지정 : 롤백
		@Transactional(rollbackFor = MyException.class)
		public void rollbackFor() throws MyException {
			log.info("call rollbackFor");
			throw new MyException();
		}

		public class MyException extends Exception {

		}

	}
}
