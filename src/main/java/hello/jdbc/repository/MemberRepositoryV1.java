package hello.jdbc.repository;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DataManager에서 DataSource 사용, JdbcUtils 사용으로
 * DriverManager는 항상 새로운 커넥션을 획득한다.
 * 그러나 DataSource를 사용하면 미리 만들어진 커넥션 풀에서 커넥션을 꺼내 사용할 수 있다.

 * 외부에서 DataSource 를 주입 받아서 사용한다. 이제 직접 만든 DBConnectionUtil 을 사용하지 않아도 된다. (dataSource.getConnection() 사용하자)
 * DataSource 는 표준 인터페이스 이기 때문에 DriverManagerDataSource 에서 HikariDataSource 로 변경되어도 해당 코드를 변경하지 않아도 된다.

 * JdbcUtils 편의 메서드
 * 스프링은 JDBC를 편리하게 다룰 수 있는 JdbcUtils 라는 편의 메서드를 제공한다.
 * JdbcUtils 을 사용하면 커넥션을 좀 더 편리하게 닫을 수 있다.
 */
@Slf4j
public class MemberRepositoryV1 {
    private final DataSource dataSource;
    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    /**
     * 보면 실행하는 스레드가 전부 0번째인데
     * 그 이유는 test 코드에서 insert, update, delete 할 때마다 connection을 반환하기 때문이다. -> 같은 커넥션 재사용
     */
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }

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
            pstmt.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money =? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate(); //select 용

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }
}