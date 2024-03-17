package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
/**
 * 기존에 직접 꺼냈던 DataSource는 javax.sql.DataSource jdbc 그냥 갖다가 쓴데다가(의존성) 동기화를 위해 파라미터로 넘겨야했지만
 * spring에서 제공하는 DataSourceUtils로 jdbc의 의존성에서 벗어날 수 있고 쉽게 커넥션을 동기화할 수 있다.
 */
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 매니저 이용해서 -> 파라미터 없이 커넥션 동기화해보자
 * DataSourceUtils.getConnection(); -> 매니저 접근
 * DataSourceUtils.releaseConnection(); -> 매니저 닫기

 * DataSourceUtils.getConnection()
 * DataSource.getConnection() 에서 DataSourceUtils.getConnection() 를 사용하도록 변경된 부분을 특히 주의해야 한다.

 * DataSourceUtils.getConnection()은 다음과 같이 동작한다.
 * 트랜잭션 동기화 매니저가 관리하는 커넥션이 있으면 해당 커넥션을 반환한다.
 * (트랜잭션 매니저 없이 할 건데? > 동기화 매니저에 조회하면 커넥션 없을 텐데 -> 없을 경우 새 커넥션을 생성해서 반환한다.)
 * 트랜잭션 동기화 매니저가 관리하는 커넥션이 없는 경우 새로운 커넥션을 생성해서 반환한다. (그러니까 한 로직이 매니저를 사용하지 않는 방식을 사용할 시)

 * DataSourceUtils.releaseConnection()
 * close() 에서 DataSourceUtils.releaseConnection() 를 사용하도록 변경된 부분을 특히 주의해야한다.
 * 커넥션을 con.close() 를 사용해서 직접 닫아버리면 커넥션이 유지되지 않는 문제가 발생한다.
 * 이 커넥션은 이후 로직은 물론이고, 트랜잭션을 종료(커밋, 롤백)할 때 까지 살아있어야 한다. (서비스가 닫아야하니까)

 * DataSourceUtils.releaseConnection() 을 사용하면 트랜잭션이 끝나도 리포지토리에서 커넥션을 바로 닫지 않는다.
 * 해당 트랜잭션은 비즈니스 로직에서 실행했기 때문에 동기화된 커넥션을 닫지 않고 그대로 유지해준다.
 * 트랜잭션 동기화 매니저가 관리하는 커넥션이 없는 경우 트랜잭션 종료되면 해당 커넥션을 그냥 닫는다.
 */
@Slf4j
public class MemberRepositoryV3 {
    private final DataSource dataSource;
    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
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
            rs = pstmt.executeQuery(); //select 용

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
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        /**
         * 트랜잭션 동기화 하려면 DataSourceUtils 를 사용해야한다.
         */
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        /**
         * 주의! 트랜잭션 동기화 하려면 DataSource가 아니라 DataSourceUtils 를 사용해야한다.
         * DataSourceUtils 에서 커넥션을 가져온다. (리포지토리에서 트랜잭션 동기화 매니저에서 트랜잭션을 막 시작한 커넥션을 가져온다.)
         * 그렇게 되면 트랜잭션 매니저가 해당 커넥션을 트랜잭션 동기화 매니저에 보관하고 여기서 커넥션을 꺼내서 사용할 수 있게 된다.
         */
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
