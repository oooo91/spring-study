package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 * 트랜잭션을 사용하는 로직을 살펴보면 다음과 같은 패턴이 반복되는 것을 확인할 수 있다.
 * 트랜잭션을 시작하고, 비즈니스 로직을 실행하고, 성공하면 커밋하고, 예외가 발생해서 실패하면 롤백한다.
 * 다른 서비스에서 트랜잭션을 시작하려면 try , catch , finally 를 포함한 성공시 커밋, 실패시 롤백 코드
 * 가 반복될 것이다.
 * 이런 형태는 각각의 서비스에서 반복된다. 달라지는 부분은 비즈니스 로직 뿐이다.
 * 이럴 때 템플릿 콜백 패턴을 활용하면 이런 반복 문제를 깔끔하게 해결할 수 있다.
 * 트랜잭션 템플릿
 * 템플릿 콜백 패턴을 적용하려면 템플릿을 제공하는 클래스를 작성해야 하는데, 스프링은
 * TransactionTemplate 라는 템플릿 클래스를 제공한다.
 * TransactionTemplate
 * public class TransactionTemplate {
 *  private PlatformTransactionManager transactionManager;
 *  public <T> T execute(TransactionCallback<T> action){..}
 *  void executeWithoutResult(Consumer<TransactionStatus> action){..}
 * }
 * execute() : 응답 값이 있을 때 사용한다.
 * executeWithoutResult() : 응답 값이 없을 때 사용한다.
 * 트랜잭션 템플릿을 사용해서 반복하는 부분을 제거해보자
 *
 * 트랜잭션 템플릿 덕분에 트랜잭션을 시작하고, 커밋하거나 롤백하는 코드가 모두 제거되었다.
 * 트랜잭션 템플릿의 기본 동작은 다음과 같다.
 * 비즈니스 로직이 정상 수행되면 커밋한다.
 * 언체크 예외가 발생하면 롤백한다. 그 외의 경우 커밋한다. (체크 예외의 경우에는 커밋하는데, 이 부분
 * 은 뒤에서 설명한다.)
 * 코드에서 예외를 처리하기 위해 try~catch 가 들어갔는데, bizLogic() 메서드를 호출하면
 * SQLException 체크 예외를 넘겨준다. 해당 람다에서 체크 예외를 밖으로 던질 수 없기 때문에 언체크 예외
 * 로 바꾸어 던지도록 예외를 전환했다
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_2 {

    //private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;
    private final TransactionTemplate txTemplate;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
        this.txTemplate = new TransactionTemplate(transactionManager); //transactionTemplate은 그냥 클래스라 유연성이 떨어져서 이런식으로 주입받도록 함 (관례)
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        //트랜잭션 템플릿이 트랜잭션 매니저 가지고 있으 -> 템플릿 안에서 매니저 로직도 실행
        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
        /** executeWithoutResult 안에 이 로직 다 실행된다.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            //비즈니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); //성공 시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); //실패 시 롤백
            throw new IllegalStateException(e);
        } //트랜잭션 종료 시 트랜잭션 매니저가 커넥션 자동으로 닫아준다. 기존의 release(con) 없앰 */
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
