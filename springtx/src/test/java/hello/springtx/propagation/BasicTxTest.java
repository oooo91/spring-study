package hello.springtx.propagation;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@SpringBootTest
public class BasicTxTest {

	@Autowired
	PlatformTransactionManager txManager;

	@TestConfiguration
	static class Config {
		@Bean
		public PlatformTransactionManager transactionManager(DataSource dataSource) {
			return new DataSourceTransactionManager(dataSource); //DataSource 가 커넥션 풀이냐 아니냐 인터페이스, DataSourceTransactionManager 트랜잭션 매니저
		}
	}

	@Test
	void commit() {
		log.info("트랜잭션 시작");
		TransactionStatus status = txManager.getTransaction( //커넥션 풀에서 (또는 지가 만든) 커넥션을 가져와서 트랜잭션을 시작한다.
			new DefaultTransactionDefinition());

		log.info("트랜잭션 커밋 시작");
		txManager.commit(status); //커밋되면 커넥션 풀에 커넥션을 되돌림
		log.info("트랜잭션 커밋 완료");
	}

	@Test
	void rollback() {
		log.info("트랜잭션 시작");
		TransactionStatus status = txManager.getTransaction(
			new DefaultTransactionDefinition());

		log.info("트랜잭션 롤백 시작");
		txManager.rollback(status);
		log.info("트랜잭션 롤백 완료");
	}

	@Test
	void double_commit() {
		log.info("트랜잭션1 시작");
		TransactionStatus tx1 = txManager.getTransaction(new
			DefaultTransactionAttribute());

		log.info("트랜잭션1 커밋");
		txManager.commit(tx1);

		log.info("트랜잭션2 시작");
		TransactionStatus tx2 = txManager.getTransaction(new
			DefaultTransactionAttribute());

		log.info("트랜잭션2 커밋");
		txManager.commit(tx2);
	}

	@Test
	void double_commit_rollback() {
		log.info("트랜잭션1 시작");
		TransactionStatus tx1 = txManager.getTransaction(new
			DefaultTransactionAttribute());

		log.info("트랜잭션1 커밋");
		txManager.commit(tx1);

		log.info("트랜잭션2 시작");
		TransactionStatus tx2 = txManager.getTransaction(new
			DefaultTransactionAttribute());

		log.info("트랜잭션2 롤백");
		txManager.rollback(tx2);
	}

	//트랜잭션 참여
	@Test
	void inner_commit() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		log.info("내부 트랜잭션 시작"); //ctrl + alt + n
		TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);

		log.info("외부 트랜잭션 커밋");
		txManager.commit(outer);
	}

	//트랜잭션 참여
	@Test
	void outer_rollback() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		log.info("내부 트랜잭션 시작"); //ctrl + alt + n
		TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);

		log.info("외부 트랜잭션 롤백");
		txManager.rollback(outer);
	}

	@Test
	void inner_rollback() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		log.info("내부 트랜잭션 시작"); //ctrl + alt + n
		TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner); //rollback-only 표기

		log.info("외부 트랜잭션 롤백");
		txManager.rollback(outer);
	}

	@Test
	void inner_rollback_requires_new() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("outer.isNewTransaction()={}", outer.isNewTransaction()); //true

		log.info("내부 트랜잭션 시작");
		DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		//TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute()); //기전 트랜잭션 참여
		TransactionStatus inner = txManager.getTransaction(definition); //새 트랜잭션 생성
		log.info("outer.isNewTransaction()={}", inner.isNewTransaction()); //true

		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner);

		log.info("외부 트랜잭션 커밋");
		txManager.rollback(outer);

	}
}
