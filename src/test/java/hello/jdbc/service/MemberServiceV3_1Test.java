package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 - 커넥션 파라미터 전달 -> 커넥션 동기화 (같은 커넥션 사용하도록)
 */
class MemberServiceV3_1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);

        /**
         * 매니저 만들 떄 왜 datasource가 필요하냐
         * new DataSourceTransactionManager(dataSource);
         * 트랜잭션 매니저가 datasource를 가지고 커넥션을 만드는 일도 하기 때문에 파라미터로 넣어줘야한다.
         * dataSource가 있어야 커넥션 만들 수 있다.

         * 트랜잭션 매니저1 - 트랜잭션 시작
         * 클라이언트의 요청으로 서비스 로직을 실행한다.
         * 1. 서비스 계층에서 transactionManager.getTransaction() 을 호출해서 트랜잭션을 시작한다.
         * 2. 트랜잭션을 시작하려면 먼저 데이터베이스 커넥션이 필요하다.
         * 3. 트랜잭션 매니저는 내부에서 데이터소스를 사용해서 커넥션을 생성한다.
         * 3. 커넥션을 수동 커밋 모드로 변경해서 실제 데이터베이스 트랜잭션을 시작한다. (setAutoCommit(false))
         * 4. 커넥션을 트랜잭션 동기화 매니저에 보관한다.
         * 5. 트랜잭션 동기화 매니저는 쓰레드 로컬에 커넥션을 보관한다. 따라서 멀티 쓰레드 환경에 안전하게 커넥션을 보관할 수 있다.

         * 트랜잭션 매니저2 - 로직 실행
         * 6. 서비스는 비즈니스 로직을 실행하면서 리포지토리의 메서드들을 호출한다. 이때 커넥션을 파라미터로 전달하지 않는다.
         * 7. 리포지토리 메서드들은 트랜잭션이 시작된 커넥션이 필요하다.
         * 리포지토리는 DataSourceUtils.getConnection() 을 사용해서 트랜잭션 동기화 매니저에 보관된 동기화된 커넥션을 조회해서 사용한다.
         * 이 과정을 통해서 자연스럽게 같은 커넥션을 사용하고, 트랜잭션도 유지된다.
         * 8. 획득한 커넥션을 사용해서 SQL을 데이터베이스에 전달해서 실행한다.

         * 트랜잭션 매니저3 - 트랜잭션 종료
         * 비즈니스 로직이 끝나고 트랜잭션을 종료한다. 트랜잭션은 커밋하거나 롤백하면 종료된다. (transactionManager.commit() or rollback())
         * 10. 트랜잭션을 종료하려면 동기화된 커넥션이 필요하다. 트랜잭션 동기화 매니저를 통해 동기화된 커넥션을 획득한다.
         * 11. 획득한 커넥션을 통해 데이터베이스에 트랜잭션을 커밋하거나 롤백한다.
         * 12. 전체 리소스를 정리한다.
         * 트랜잭션 동기화 매니저를 정리한다. 쓰레드 로컬은 사용후 꼭 정리해야 한다.
         * con.setAutoCommit(true) 로 되돌린다. 커넥션 풀을 고려해야 한다.
         * con.close() 를 호출해셔 커넥션을 종료한다. 커넥션 풀을 사용하는 경우 con.close() 를 호출하면 커넥션 풀에 반환된다.
         */
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_1(transactionManager, memberRepository); //트랜잭션 동기화 매니저 생성

    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }


    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        //when
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalAccessError.class);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);

    }
}