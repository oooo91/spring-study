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

    //계좌이체
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        //커넥션을 얻어야
        try {
            con.setAutoCommit(false); //수동 커밋 = 트랜잭션 시작

            bizLogic(con, fromId, toId, money);

            //커넥션에서 성공 시 커밋
            con.commit();
        } catch (Exception e) {
          con.rollback(); //실패 시 롤백
          throw new IllegalStateException(e);
        } finally {
            if (con != null) {
                try {
                    /**
                     * 그냥 con.close() 하면 풀에 돌아가게 되면 setAutoCommit이 false인 채로 돌아가면,
                     * 누가 이 커넥션을 획득했을 때 false인 채로 받는데, 기본갑시 true이기 때문에 fsle면 문제가 생길 수 있어서 다시 기본갓으로 돌려놓자
                     *
                     */
                    con.setAutoCommit(true); //커넥션 풀 고려
                    con.close();
                } catch (Exception e) {
                    log.info("error", e); //exception은 로그 = {} 이거 안함 그냥 e 넣으면 됨
                }
            }
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직
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
