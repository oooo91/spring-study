package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 프록시를 도입하기 전에는 기존처럼 서비스의 로직에서 트랜잭션을 직접 시작한다.
 * 그런데 프록시를 사용하면 트랜잭션을 처리하는 객체와 비즈니스 로직을 처리하는 서비스 객체를 명확하게 분리할 수 있다.
 * 프록시 호출하면 프록시가 트랜잭션 처리 로직 (트랜잭션 시작, 종료)을 대리 수행한다.
 * 비즈니스 로직은 비즈니스 로직에만 집중할 수 있다.

 * 스프링이 제공하는 트랜잭션 AOP
 * 스프링이 제공하는 AOP 기능을 사용하면 프록시를 매우 편리하게 적용할 수 있다.
 * 스프링 핵심 원리 - 고급편을 통해 AOP를 열심히 공부하신 분이라면 아마도 @Aspect , @Advice , @Pointcut 를 사용해서
 * 트랜잭션 처리용 AOP를 어떻게 만들지 머리속으로 그림이 그려질 것이다.

 * 물론 스프링 AOP를 직접 사용해서 트랜잭션을 처리해도 되지만, 트랜잭션은 매우 중요한 기능이고, 전세계 누구나 다 사용하는 기능이다.
 * 스프링은 트랜잭션 AOP를 처리하기 위한 모든 기능을 제공한다. 스프링 부트를 사용하면 트랜잭션 AOP를 처리하기 위해 필요한 스프링 빈들도 자동으로 등록해준다.
 * 개발자는 트랜잭션 처리가 필요한 곳에 @Transactional 애노테이션만 붙여주면 된다.
 * 스프링의 트랜잭션 AOP는 이 애노테이션을 인식해서 트랜잭션 프록시를 적용해준다.
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    /**
     * 이 메서드 호출할 때 트랜잭션 걸겠다.
     * 런타임 터지면 롤백하겠다.
     */
    @Transactional //얘가 없으면 예외 터져도 롤백이 안돼서 10000, 10000이 아니라 8000, 10000이 된다.
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
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
