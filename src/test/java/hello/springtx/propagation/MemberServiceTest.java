package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

@Slf4j
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	LogRepository logRepository;

	/**
	 * MemberService @Transactional: OFF
	 * memberRepository @Transactional: ON
	 * logRepository @Transactional: ON
	 */
	@Test
	void outerTxOff_success() {
		//given
		String username = "outerTxOff_success";

		//when
		memberService.joinV1(username);

		//then : 모든 데이터가 정장 저장된다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * MemberService @Transactional: OFF
	 * memberRepository @Transactional: ON
	 * logRepository @Transactional: ON -> RuntimeException -> rollback
	 */
	@Test
	void outerTxOff_fail() {
		//given
		String username = "로그예외 outerTxOff_success";

		//when
		assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * MemberService @Transactional: ON => 이래 버리면 외부 트랜잭션(물리) + 내부 트랜잭션가 되겠군 -> 즉, 서비스에 추가하면 모든 트랜잭션은 같은 커넥션을 사용한다.
	 * memberRepository @Transactional: OFF
	 * logRepository @Transactional: OFF
	 */
	@Test
	void singleTx() {
		//given
		String username = "outerTxOff_success";

		//when
		memberService.joinV1(username);

		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * MemberService @Transactional: ON => 이래 버리면 외부 트랜잭션(물리) + 내부 트랜잭션가 되겠군 -> 즉, 서비스에 추가하면 모든 트랜잭션은 같은 커넥션을 사용한다.
	 * memberRepository @Transactional: ON
	 * logRepository @Transactional: ON
	 */
	@Test
	void outerTxOn_success() {
		//given
		String username = "outerTxOff_success";

		//when
		memberService.joinV1(username);

		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * MemberService @Transactional: ON => 전체가 다 롤백된다. => 데이터 정합성 보장
	 * memberRepository @Transactional: ON
	 * logRepository @Transactional: ON => Exception
	 */
	@Test
	void outerTxOn_fail() {
		//given
		String username = "로그예외 outerTxOff_success";

		//when
		assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

		//then : 모든 데이터가 롤백된다.
		assertTrue(memberRepository.find(username).isEmpty());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * MemberService @Transactional: ON => 전체가 다 롤백된다. => 데이터 정합성 보장
	 * memberRepository @Transactional: ON
	 * logRepository @Transactional: ON => Exception 일 때 setRollback-only 표기를 하고 서비스로 넘어가서 => service 에서는 커밋을 하지만 실제로는 롤백이 된다.
	 */
	@Test
	void recoverException_fail() {
		//given
		String username = "로그예외 outerTxOff_success";

		//when
		assertThatThrownBy(() -> memberService.joinV2(username)).isInstanceOf(
			UnexpectedRollbackException.class);

		//then : 모든 데이터가 롤백된다.
		assertTrue(memberRepository.find(username).isEmpty());
		assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * MemberService @Transactional: ON
	 * memberRepository @Transactional: ON
	 * logRepository @Transactional: ON(REQUIRES_NEW) Exception
	 */
	@Test
	void recoverException_success() {
		//given
		String username = "로그예외 outerTxOff_success";

		//when
		memberService.joinV2(username);

		//then : 모든 데이터가 롤백된다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isEmpty());
	}
}