package hello.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    /**
     * 정리
     * 회원 데이터를 DB에 보관하고 관리하기 위해 앞서 빈으로 등록한 JdbcTemplate , DataSource , TransactionManager 가 모두 사용되었다.
     * 그런데 생각해보면 DB에 데이터를 보관하고 관리하기 위해 이런 객체들을 항상 스프링 빈으로 등록해야 하는 번거로움이 있다.
     * 만약 DB를 사용하는 다른 프로젝트를 진행한다면 이러한 객체들을 또 스프링 빈으로 등록해야 할 것이다. -> 자동완성으로 해결하자

     * DbConfig에 @Configuration 삭제하여 빈 등록이 안되도록 했다.
     * 우리가 등록한 JdbcTemplate, DataSource, TransactionManager 가 분명히 스프링 빈으로 등록되지 않았다는 것이다.
     * 그런데 테스트는 정상 통과하고 심지어 출력결과에 JdbcTemplate, DataSource, TransactionManager 빈들이 존재하는 것을 확인할 수 있다.
     * 사실 이 빈들은 모두 스프링 부트가 자동으로 등록해 준 것이다.
     * 이러한 자동 구성 덕분에 개발자는 반복적이고 복잡한 빈 등록과 설정을 최소화 하고 애플리케이션 개발을 빠르게 시작할 수 있다.
     * spring boot starter 안에 autoconfigurer 안에 자동완성기능이 들어가있다.

     * 스프링 부트가 제공하는 자동 구성 기능을 이해하려면 다음 두 가지 개념을 이해해야 한다.
     * @Conditional : 특정 조건에 맞을 때 설정이 동작하도록 한다.
     * @AutoConfiguration : 자동 구성이 어떻게 동작하는지 내부 원리 이해
     */
    @Transactional
    @Test
    void memberTest() {
        Member member = new Member("idA", "memberA");
        memberRepository.initTable();
        memberRepository.save(member);
        Member findMember = memberRepository.find(member.getMemberId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
        Assertions.assertThat(findMember.getMemberId()).isEqualTo(member.getMemberId());
    }
}