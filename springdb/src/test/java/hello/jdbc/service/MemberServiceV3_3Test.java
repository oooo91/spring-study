package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @SpringBootTest
 * 지금까지 TEST는 스프링과 독립된 환경이었는데
 * 스프링 AOP를 테스트에 적용하려면 스프링 컨테이너를 띄워서 빈(@Bean)을 주입해야한다. 이때 @SpringBootTest 어노테이션 붙이면 된다.
 * 그리고 @Autowired 등을 통해 스프링 컨테이너가 관리하는 빈들을 사용할 수 있다.

 * @TestConfiguration
 * 테스트 안에서 내부 설정 클래스를 만들어서 사용하면서 이 에노테이션을 붙이면,
 * 스프링 부트가 자동으로 만들어주는 빈들에 추가로 필요한 스프링 빈들을 등록하고 테스트를 수행할 수 있다.
 */
@Slf4j
@SpringBootTest //spring이 필요한 spring을 띄우게 된다. 의존관계 주입을 시킬 수 있다.
class MemberServiceV3_3Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;

    @Autowired
    private MemberServiceV3_3 memberService;

    /**
     * 프록시가 쓰는 빈들 등록 -> 프록시가 매니저 부르기 때문에 매니저도 등록해야한다.

     * TestConfig
     * DataSource -> 스프링에서 기본으로 사용할 데이터소스를 스프링 빈으로 등록한다.
     * 추가로 트랜잭션 매니저에서도 사용한다.
     * DataSourceTransactionManager -> 트랜잭션 매니저를 스프링 빈으로 등록한다.
     * 스프링이 제공하는 트랜잭션 AOP는 스프링 빈에 등록된 트랜잭션 매니저를 찾아서 사용하기 때문에 트랜잭션 매니저를 스프링 빈으로 등록해두어야 한다
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        /**
         * 트랜잭션이 시작된다는 뜻은
         * 매니저가 커넥션 만들고 수동으로 설정(setAutoCommit=false)하고 커넥션 동기화를 위해 트랜잭션 동기화 매니저에 커넥션을 보관한다는 것과 같다.
         * 그럼 프록시에서 실제 서비스 호출하고 리포지토리는 동기화 매니저에 있는 커넥션을 꺼낸다.
         * 쿼리 수행되고 리턴되면 트랜잭션 처리 로직에서 런타임 발생 시 롤백 성공 시 커밋하고 반환한다.
         * 이걸 @Transaction 하나로 스프링이 다 처리한다.
         */
        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    void AopCheck() {
        /**
         * 프폭시 memberService 객체 -> 트랜잭션도 실제 비즈니스 로직(target)을 호출하는 코드를 가지고 있다.
         */
        log.info("memberService class={}", memberService.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberService)).isTrue();
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