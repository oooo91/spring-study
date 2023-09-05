package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //수동 커밋 = 트랜잭션 시작
            bizLogic(con, fromId, toId, money);
            con.commit(); //커넥션에서 성공 시 커밋
        } catch (Exception e) {
          con.rollback(); //실패 시 롤백
          throw new IllegalStateException(e);
        } finally {
            if (con != null) {
                try {
                    /**
                     * 그냥 con.close() 하면 풀에 돌아가게 되면 setAutoCommit이 false인 채로 돌아가게 되는데
                     * 누가 이 커넥션을 획득했을 때 false인 채로 받기 때문에
                     * 돌아갈 때 다시 기본값인 true로 변경하고 돌려놓는 것이 좋다.
                     */
                    con.setAutoCommit(true); //커넥션 풀 고려
                    con.close(); //서비스에서 닫아야한다.
                } catch (Exception e) {
                    log.info("error", e); //exception은 로그 = {} 이거 안함 그냥 e 넣으면 됨
                }
            }
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember); //오류 케이스
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
