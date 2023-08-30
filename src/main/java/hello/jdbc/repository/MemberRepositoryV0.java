package hello.jdbc.repository;

import static hello.jdbc.connection.DBConnectionUtil.*;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        //커넥션 가져오기
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); //실행, update 행 개수 반환

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            /**
             * 만약에 여기서 exception이 터지면 con.close() 호출이 안 되는 문제 발생
             * tcp/ip 커넥션 끊기
             * pstmt.close();
             * con.close();
             * 꼭 끊어야함 안 그러면 리소스 누수 발생
             */
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                //커넥션 닫을 때 오류 터진 거라 할 수 있는 마땅한 조치가 없다
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }
}
