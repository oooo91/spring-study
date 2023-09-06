package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가하기

 * 데이터 접근 예외 직접 만들기
 * 발단
 * 데이터베이스에서 어떤 예외 상황이든 항상 같은 SQLException 이 떨어진다.
 * 데이터베이스는 에러 상황마다 각기 다른 코드를 제공한다.
 * 예를 들어 ID 중복인 경우, 데이터베이스는 SQLException과 함께 '23505' 코드를 반환한다.
 * 이 코드를 받아서 런타임 예외로 처리하여 해결할 수 있다.
 * e.getErrorCode() == 23505 (h2는 23505를 반환한다.) (스프링이 ErrorCode를 지원한다.)

 * 그런데 두 번째 문제
 * DB마다 에러 코드가 다르다.
 * 스프링은 앞서 설명한 문제들을 해결하기 위해 데이터 접근과 관련된 예외를 추상화해서 제공한다.
 * 스프링은 데이터 접근 계층에 대한 수십 가지 예외를 정리해서 일관된 예외 계층을 제공한다.
 * 각각의 예외는 특정 기술에 종속적이지 않게 설계되어있어, 서비스 계층에서도 스프링이 제공하는 예외를 사용하면 된다.
 * 예를 들어서 JDBC 기술을 사용하든, JPA 기술을 사용하든 스프링이 제공하는 예외를 사용하면 된다.
 * 뿐만 아니라 JDBC나 JPA를 사용할 때 발생하는 예외를 스프링이 제공하는 예외로 변환해주는 역할도 스프링이 제공한다.

 * 예외 변환기
 * 스프링은 데이터베이스에서 발생하는 오류 코드를 스프링이 정의한 예외로 자동으로 변환해주는 변환기를 제공한다.
 * 왜 예외 변환기가 필요하면 스프링이 추상화한 예외 클래스를 제공한들, 결국 코드와 상황에 맞는 예외 클래스 찾아 직접 던지는 건 변함없다.
 * 이전에 살펴봤던 SQL ErrorCode를 직접 확인하는 방법이다. int errorCode = e.getErrorCode();
 * 이렇게 직접 예외를 확인하고 하나하나 스프링이 만들어준 예외로 변환하는 것은 현실성이 없다.
 * 이렇게 하려면 해당 오류 코드를 확인하고 스프링의 예외 체계에 맞추어 예외를 직접 변환해야 할 것이다.
 * 그리고 데이터베이스마다 오류 코드가 다르다는 점도 해결해야 한다.
 * 그래서 스프링은 예외 변환기를 제공한다.

 * 어떻게 변환할 수 있었을까?
 * SQL 문법이 잘못되어 BadSqlGrammarException 을 반환한다고 가정하자.
 * 눈에 보이는 반환 타입은 최상위 타입인 DataAccessException 이지만 실제로는 BadSqlGrammarException 예외가 반환된다.

 * 각각의 DB마다 SQL ErrorCode는 다르다.
 * 그런데 스프링은 어떻게 각각의 DB가 제공하는 SQL ErrorCode까지 고려해서 예외를 변환할 수 있을까
 * 비밀은 바로 다음 XML 파일에 있다.
 * sql-error-codes.xml
 * <bean id="H2" class="org.springframework.jdbc.support.SQLErrorCodes">
 * <property name="badSqlGrammarCodes">
 * <value>42000,42001,42101,42102,42111,42112,42121,42122,42132</value>
 * </property>
 * <property name="duplicateKeyCodes">
 * <value>23001,23505</value>
 * </property>
 * </bean>
 * <bean id="MySQL" class="org.springframework.jdbc.support.SQLErrorCodes">
 * <property name="badSqlGrammarCodes">
 * <value>1054,1064,1146</value>
 * </property>
 * <property name="duplicateKeyCodes">
 * <value>1062</value>
 * </property>
 * </bean>
 * org.springframework.jdbc.support.sql-error-codes.xml
 * 스프링 SQL 예외 변환기는 SQL ErrorCode를 이 파일에 대입해서 어떤 스프링 데이터 접근 예외로 전환해야 할지 찾아낸다.
 * 예를 들어서 H2 데이터베이스에서 42000 이 발생하면 badSqlGrammarCodes 이기 때문에 BadSqlGrammarException 을 반환한다.
 * 해당 파일을 확인해보면 10개 이상의 우리가 사용하는 대부분의 관계형 데이터베이스를 지원하는 것을 확인할 수 있다.
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exceptionTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            //throw e;
            //체크 예외를 런타임 예외로 변경하기, 이때 로그 잘 찍히게 기존 예외 담아놓자
            //throw new MyDbException(e);
            /**
             * translator가 알아서 에외 넘기도록 하기
             */
            throw exceptionTranslator.translate("save", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) {
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
            throw exceptionTranslator.translate("save", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }

    public void update(String memberId, int money) {
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
            throw exceptionTranslator.translate("save", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }


    public void delete(String memberId) {
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
            throw exceptionTranslator.translate("save", sql, e);
        } finally {
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {

        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
