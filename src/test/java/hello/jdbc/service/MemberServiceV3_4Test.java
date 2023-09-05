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
 * 트랜잭션 - DataSource, transactionManager 자동 등록 (application.properties에 datasource 정보만 넣어두면 알아서 주입해준다.)
 *
 *
 * 스프링 부트의 자동 리소스 등록
 * 스프링 부트가 등장하기 이전에는 데이터소스와 트랜잭션 매니저를 개발자가 직접 스프링 빈으로 등록해서
 * 사용했다. 그런데 스프링 부트로 개발을 시작한 개발자라면 데이터소스나 트랜잭션 매니저를 직접 등록한
 * 적이 없을 것이다.
 * 이 부분을 잠시 살펴보자
 * 기존에는 이렇게 데이터소스와 트랜잭션 매니저를 직접 스프링 빈으로 등록해야 했다. 그런데 스프링 부트
 * 가 나오면서 많은 부분이 자동화되었다. (더 오래전에 스프링을 다루어왔다면 해당 부분을 주로 XML로 등
 * 록하고 관리했을 것이다.)
 * 데이터소스 - 자동 등록
 * 스프링 부트는 데이터소스( DataSource )를 스프링 빈에 자동으로 등록한다.
 * 자동으로 등록되는 스프링 빈 이름: dataSource
 * 참고로 개발자가 직접 데이터소스를 빈으로 등록하면 스프링 부트는 데이터소스를 자동으로 등록하지 않는
 * 다.
 * 이때 스프링 부트는 다음과 같이 application.properties 에 있는 속성을 사용해서 DataSource 를 생
 * 성한다. 그리고 스프링 빈에 등록한다.
 * application.properties
 * spring.datasource.url=jdbc:h2:tcp://localhost/~/test
 * spring.datasource.username=sa
 * spring.datasource.password=
 * 스프링 부트가 기본으로 생성하는 데이터소스는 커넥션풀을 제공하는 HikariDataSource 이다. 커넥션풀
 * 과 관련된 설정도 application.properties 를 통해서 지정할 수 있다.
 * spring.datasource.url 속성이 없으면 내장 데이터베이스(메모리 DB)를 생성하려고 시도한다.
 * 트랜잭션 매니저 - 자동 등록
 * 스프링 부트는 적절한 트랜잭션 매니저( PlatformTransactionManager )를 자동으로 스프링 빈에 등록한
 * 다.
 * 자동으로 등록되는 스프링 빈 이름: transactionManager
 * 참고로 개발자가 직접 트랜잭션 매니저를 빈으로 등록하면 스프링 부트는 트랜잭션 매니저를 자동으로 등록
 * 하지 않는다.
 * 어떤 트랜잭션 매니저를 선택할지는 현재 등록된 라이브러리를 보고 판단하는데, JDBC를 기술을 사용하면
 * DataSourceTransactionManager 를 빈으로 등록하고, JPA를 사용하면 JpaTransactionManager 를
 * 빈으로 등록한다. 둘다 사용하는 경우 JpaTransactionManager 를 등록한다. 참고로
 * JpaTransactionManager 는 DataSourceTransactionManager 가 제공하는 기능도 대부분 지원한다.
 */
@Slf4j
@SpringBootTest //spring이 필요한 spring을 띄우게 된다. 의존관계 주입을 시킬 수 있다.
class MemberServiceV3_4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;

    @Autowired
    private MemberServiceV3_3 memberService;

    //프록시가 쓰는 빈들 등록 -> 프록시가 매니저 부름
    @TestConfiguration
    static class TestConfig {

        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        /**
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }
        */
        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource);
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
        log.info("memberService class={}", memberService.getClass()); //프폭시 memberService 객체 -> 트랜잭션도 실제 비즈니스 로직(target)을 호출하는 코드 가지고 있다.
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