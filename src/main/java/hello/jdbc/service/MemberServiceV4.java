package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스 의존
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV4 {

    //인터페이스
    private final MemberRepository memberRepository;

    /**
     * 이 메서드 호출할 때 트랜잭션 걸겠다.
     * 런타임 터지면 롤백하겠다.
     */
    @Transactional //얘가 없으면 예외 터져도 롤백이 안돼서 10000, 10000이 아니라 8000, 10000이 된다.
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    //throws SQLException 제거되었음 (실제 체크 예외를 런타임 예외로 바꿨기 때문이다)
    private void bizLogic(String fromId, String toId, int money) {
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
