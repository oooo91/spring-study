package hello.jdbc.transaction;

public class Problem {
    /**
     * 문제 정리
     * 지금까지 우리가 개발한 애플리케이션의 문제점은 크게 3가지이다.
     * 트랜잭션 문제
     * 예외 누수 문제
     * JDBC 반복 문제

     * 트랜잭션 문제
     * 가장 큰 문제는 트랜잭션을 적용하면서 생긴 다음과 같은 문제들이다.
     * JDBC 구현 기술이 서비스 계층에 누수되는 문제 -> 트랜잭션을 적용하기 위해 JDBC 구현 기술이 서비스 계층에 누수되었다.
     * 서비스 계층은 순수해야 한다. 구현 기술을 변경해도 서비스 계층 코드는 최대한 유지할 수 있어야한다. (변화에 대응)
     * 그래서 데이터 접근 계층에 JDBC 코드를 다 몰아두는 것이 옳다.

     * 물론 데이터 접근 계층의 구현 기술이 변경될 수도 있으니 데이터 접근 계층은 인터페이스를 제공하는 것이 좋다.
     * 서비스 계층은 특정 기술에 종속되지 않아야 한다.
     * 지금까지 그렇게 노력해서 데이터 접근 계층으로 JDBC 관련 코드를 모았는데,
     * 트랜잭션을 적용하면서 결국 서비스 계층에 JDBC 구현 기술의 누수가 발생했다.

     * 트랜잭션 동기화 문제
     * 같은 트랜잭션을 유지하기 위해 커넥션을 파라미터로 넘겨야 한다.
     * 이때 파생되는 문제들도 있다.
     * 똑같은 기능도 트랜잭션용 기능과 트랜잭션을 유지하지 않아도 되는 기능으로 분리해야 한다.

     * 트랜잭션 적용 반복 문제
     * 트랜잭션 적용 코드를 보면 반복이 많다. try , catch , finally ...

     * 예외 누수
     * 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파된다.
     * SQLException 은 체크 예외이기 때문에 데이터 접근 계층을 호출한 서비스 계층에서 반드시 해당 예외를 잡아서 처리하거나
     * 명시적으로 throws 를 통해서 다시 밖으로 던져야한다.
     * 또한 SQLException 은 JDBC 전용 기술이다.
     * 향후 JPA나 다른 데이터 접근 기술을 사용하면, 그에 맞는 다른 예외로 변경해야 하고, 결국 서비스 코드도 수정해야 한다.

     * JDBC 반복 문제
     * 지금까지 작성한 MemberRepository 코드는 순수한 JDBC를 사용했다.
     * 이 코드들은 유사한 코드의 반복이 너무 많다.
     * try , catch , finally ...
     * 커넥션을 열고, PreparedStatement 를 사용하고, 결과를 매핑하고... 실행하고, 커넥션과 리소스를 정리한다.

     * 스프링과 문제 해결
     * 스프링은 서비스 계층을 순수하게 유지하면서, 지금까지 이야기한 문제들을 해결할 수 있는 다양한 방법과 기술들을 제공한다.
     * 지금부터 스프링을 사용해서 우리 애플리케이션이 가진 문제들을 하나씩 해결해보자

     * 1. 트랜잭션 누수 해결 -> 스프링이 트랜잭션 추상화를 제공한다
     * 현재 서비스 계층은 트랜잭션을 사용하기 위해서 JDBC 기술에 의존하고 있다.
     * 향후 JDBC에서 JPA 같은 다른 데이터 접근 기술로 변경하면, 서비스 계층의 트랜잭션 관련 코드도 모두 함께 수정해야 한다.

     * 구현 기술에 따른 트랜잭션 사용법
     * 트랜잭션은 원자적 단위의 비즈니스 로직을 처리하기 위해 사용한다.
     * 구현 기술마다 트랜잭션을 사용하는 방법이 다르다.
     * JDBC : con.setAutoCommit(false)
     * JPA : transaction.begin()

     * 트랜잭션 추상화 인터페이스
     * public interface TxManager {
     *  begin();
     *  commit();
     *  rollback();
     * }

     * 트랜잭션은 사실 단순하다. 트랜잭션을 시작하고, 비즈니스 로직의 수행이 끝나면 커밋하거나 롤백하면 된다.
     * 그리고 다음과 같이 TxManager 인터페이스를 기반으로 각각의 기술에 맞는 구현체를 만들면 된다.
     * JdbcTxManager : JDBC 트랜잭션 기능을 제공하는 구현체
     * JpaTxManager : JPA 트랜잭션 기능을 제공하는 구현체
     * 따라서 나는 이 인터페이스에 맞게 구현체를 갈아끼워넣기만 하면 된다.

     * 2. 트랜잭션 동기화 문제 해결 -> 트랜잭션 동기화 매니저 제공
     * 스프링은 트랜잭션 동기화 매니저를 제공한다.
     * 이것은 쓰레드 로컬( ThreadLocal )을 사용해서 커넥션을 동기화해준다.
     * 트랜잭션 매니저는 내부에서 이 트랜잭션 동기화 매니저를 사용한다.
     * 트랜잭션 동기화 매니저는 쓰레드 로컬을 사용하기 때문에 멀티쓰레드 상황에 안전하게 커넥션을 동기화 할 수 있다.
     * 따라서 커넥션이 필요하면 트랜잭션 동기화 매니저를 통해 커넥션을 획득하면 된다. 따라서 이전처럼 파라미터로 커넥션을 전달하지 않아도 된다.

     * 이전에는 내가 커넥션을 들고 있었다면 동기화 매니저에 이제 커넥션을 보관한다.
     * 리포지토리가 커넥션을 필요로 할 때 동기화 매니저에 있는 커넥션을 꺼낸다. 따라서 파라미터로 커넥션을 전달하지 않아도 된다.
     */
}
