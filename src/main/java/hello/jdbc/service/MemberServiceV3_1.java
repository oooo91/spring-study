package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

    /**
     * PlatformTransactionManager 주입
     */
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;
    //private final DataSource dataSource;

    //계좌이체
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //트랜잭션 시작, 파라미터 : 트랜잭션 속성
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            //비즈니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); //성공 시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); //실패 시 롤백
            throw new IllegalStateException(e);
        } //트랜잭션 종료 시 매니저가 커넥션 자동으로 닫아준다.
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직
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
