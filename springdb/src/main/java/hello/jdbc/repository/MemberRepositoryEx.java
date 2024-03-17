package hello.jdbc.repository;

import hello.jdbc.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryEx {
    /**
     * 체크 예외의 단점을 구현 클래스에서 예외를 처리할 거면 상속받는 인터페이스에서도 예외를 던져줘야한다.
     * 그리고 이것 때문에 인터페이스도 수정해야한다. 인터페이스는 수정 안하려고 만든 건데 말이애.
     * 또한 특정 기술에 종속되는 인터페이스가 되어버린다. (SQLException -> jdbc 기술에 종속적)
     * 그렇다고 Exception을 던지기에는 하위 예외가 다 떨어질 뿐더러 명확하지도 않아서 좋지 못하다.
     */
    Member save(Member member) throws SQLException;
    Member findById(String memberId);
    void update(String memberId, int money);
    void delete(String memberId);
}
