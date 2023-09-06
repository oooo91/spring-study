package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * jdbc 템플릿 사용 -> jdbc 반복 문제 해결

 * JDBC 반복 문제
 * 커넥션 조회, 커넥션 동기화
 * PreparedStatement 생성 및 파라미터 바인딩
 * 쿼리 실행
 * 결과 바인딩
 * 예외 발생시 스프링 예외 변환기 실행
 * 리소스 종료

 * JdbcTemplate 은 JDBC로 개발할 때 발생하는 반복을 대부분 해결해준다.
 * 그 뿐만 아니라 지금까지 학습했던, 트랜잭션을 위한 커넥션 동기화는 물론이고, 예외 발생시 스프링 예외 변환기도 자동으로 실행해준다.

 * 정리 -> 서비스 계층의 순수성
 * 트랜잭션 추상화 + 트랜잭션 AOP 덕분에 서비스 계층의 순수성을 최대한 유지하면서 서비스 계층에서 트랜잭션을 사용할 수 있다.
 * 스프링이 제공하는 예외 추상화와 예외 변환기 덕분에, 데이터 접근 기술이 변경되어도 서비스 계층의 순수성을 유지하면서 예외도 사용할 수 있다.
 * 서비스 계층이 리포지토리 인터페이스에 의존한 덕분에 향후 리포지토리가 다른 구현 기술로 변경되어도 서비스 계층을 순수하게 유지할 수 있다.
 * 리포지토리에서 JDBC를 사용하는 반복 코드가 JdbcTemplate 으로 대부분 제거되었다.
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

    private final JdbcTemplate template;
    public MemberRepositoryV5(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId);
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }
}
