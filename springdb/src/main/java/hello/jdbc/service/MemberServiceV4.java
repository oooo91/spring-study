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

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    /**
     * throws SQLException 제거되었음 (실제 체크 예외를 런타임 예외로 바꿨기 때문이다)
     */
    private void bizLogic(String fromId, String toId, int money) {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember); //오류 케이스
        memberRepository.update(toId, toMember.getMoney() + money);

        /**
         * 스프링이 예외를 추상화해준 덕분에, 서비스 계층은 특정 리포지토리의 구현 기술과 예외에 종속적이지 않게 되었다.
         * 따라서 서비스 계층은 특정 구현 기술이 변경되어도 그대로 유지할 수 있게 되었다. 다시 DI를 제대로 활용할 수 있게 된 것이다.
         * 추가로 서비스 계층에서 예외를 잡아서 복구해야 하는 경우,
         * 예외가 스프링이 제공하는 데이터 접근 예외로 변경되어서 서비스 계층에 넘어오기 때문에 필요한 경우 예외를 잡아서 복구하면 된다.
         * try {
         *   memberRepository.update(fromId, fromMember.getMoney() - money);
         * } catch (DuplicateKeyException e {
         *   //복구 로직
         * }
         */
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
