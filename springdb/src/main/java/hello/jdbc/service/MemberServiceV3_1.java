package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSource -> Spring에서 제공하는 TransactionManager 사용
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

    /**
     * PlatformTransactionManager (얘가 매니저) 주입
     * PlatformTransactionManager 인터페이스라 DI 자유롭다.
     * 예를 들어 DataSourceTransactionManager() 또는 JPA를 사용할 경우 JpaTransactionManager()를 주입하면 되겠다.
     * 전자는 springboot-start-jdbc build 시 사용 가능하고 jpa는 아마 springboot-start-jpa를 build하면 사용할 수 있다.
     */
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        /**
         * 트랜잭션 시작 -> PlatformTransactionManager 매니저 등록 후 매니저에서 .getTransaction() 실행하면 트랜잭션 시작된다.
         * 파라미터 : 트랜잭션 속성

         * 트랜잭션이 시작된다는 뜻은
         * 매니저가 커넥션 만들고 수동으로 설정(setAutoCommit=false)하고 커넥션 동기화를 위해 트랜잭션 동기화 매니저에 커넥션을 보관한다는 것과 같다.
         */
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); //성공 시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); //실패 시 롤백
            throw new IllegalStateException(e);
        } //트랜잭션 종료 시 트랜잭션 매니저가 커넥션 자동으로 닫아준다. 기존의 release(con) 없앰
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {

        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember); //오류 케이스
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
