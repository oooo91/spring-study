package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            /**
             * 원래는 데이터 베이스마다 연결 방식이 달랐는데 자바 표준 jdbc 인터페이스 기술 기반으로 방식을 통일할 수 있게 되었다.
             * 필요한 값만(URL, USERNAME, PASSWORD)만 넣어준다면
             * 각 DB 사에서 만든 Connection 인터페이스의 구현체들(드라이버)로부터 DB 연결을 할 수 있다.
             * 이로부터 개발자가 각각의 데이터베이스마다 커넥션 연결, SQL 전달, 그리고 그 결과를 응답 받는 방법을 새로 학습할 필요가 없어졌다.

             * 데이터베이스에 연결하려면 JDBC가 제공하는 DriverManager.getConnection(..) 를 사용하면 된다. (데이터베이스 커넥션 획득)
             * 이렇게 하면 라이브러리에 있는 데이터베이스 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환해준다.
             * 여기서는 H2 데이터베이스 드라이버가 작동해서 실제 데이터베이스와 커넥션을 맺고 그 결과를 반환해준다.

             * 실행 결과를 보면 (테스트 코드 확인) class=class org.h2.jdbc.JdbcConnection 부분을 확인할 수 있다.
             * 이것이 바로 H2 데이터베이스 드라이버가 제공하는 H2 전용 커넥션이다.
             * 물론 이 커넥션은 JDBC 표준 커넥션 인터페이스인 java.sql.Connection 인터페이스를 구현하고 있다.

             * 어떻게 연결이 되냐
             * build.gradle 에 h2 드라이버를 라이브러리에 등록하고
             * getConnection()이 알아서 라이브러리에 등록된 드라이버 목록들에 일단 다 커넥션 요청을 한다. (getConnection이 알아서 인지한다.)
             * 각각의 드라이버는 처리가 가능한지 확인을 한다 -> 가령 jdbc:h2로 시작하면 h2 드라이버가 처리할 것임
             */
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());

            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}

/**
 * 커넥션 풀이란?
 * 데이터베이스 커넥션은 매번 획득해야하며
 * 데이터베이스 커넥션을 획득할 때는 다음과 같은 복잡한 과정을 거친다.
 * 1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회한다.
 * 2. DB 드라이버는 DB와 TCP/IP 커넥션을 연결한다. 물론 이 과정에서 3 way handshake 같은 TCP/IP 연결을 위한 네트워크 동작이 발생한다.
 * 3. DB 드라이버는 TCP/IP 커넥션이 연결되면 ID, PW와 기타 부가정보를 DB에 전달한다.
 * 4. DB는 ID, PW를 통해 내부 인증을 완료하고, 내부에 DB 세션을 생성한다.
 * 5. DB는 커넥션 생성이 완료되었다는 응답을 보낸다.
 * 6. DB 드라이버는 커넥션 객체를 생성해서 클라이언트에 반환한다.

 * 이렇게 커넥션을 새로 만드는 것은 과정도 복잡하고 시간도 많이 많이 소모되는 일이다.
 * DB는 물론이고 애플리케이션 서버에서도 TCP/IP 커넥션을 새로 생성하기 위한 리소스를 매번 사용해야한다.
 * 진짜 문제는 고객이 애플리케이션을 사용할 때, SQL을 실행하는 시간 뿐만 아니라 커넥션을 새로 만드는 시간이 추가되기 때문에
 * 결과적으로 응답 속도에 영향을 준다. 이것은 사용자에게 좋지 않은 경험을 줄 수 있다.

 * 암튼 이런 문제를 한번에 해결하는 아이디어가 바로 커넥션을 '미리 생성해두고' 사용하는 커넥션 풀이라는 방법이다.
 * 커넥션 풀은 이름 그대로 커넥션을 관리하는 풀(수영장 풀을 상상하면 된다.)이다.
 * 앞서 학습한 JDBC DriverManager 를 직접 사용하여 커넥션을 얻는 것보다 커넥션 풀을 사용하는 것이 좋다.

 * 애플리케이션을 시작하는 시점에 커넥션 풀은 필요한 만큼 커넥션을 미리 확보해서 풀에 보관한다. (스레드 풀과 똑같네)
 * 보통 얼마나 보관할 지는 서비스의 특징과 서버 스펙에 따라 다르지만 기본값은 보통 10개이다.
 * 커넥션 풀에 들어 있는 커넥션은 TCP/IP로 DB와 커넥션이 이미 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL을 DB에 전달할 수 있다.
 * 여기서 주의할 점은 커넥션을 종료하는 것이 아니라 커넥션이 살아있는 상태로 커넥션 풀에 반환해야 한다는 것이다.

 * 대표적인 커넥션 풀 오픈소스는 commons-dbcp2 , tomcat-jdbc pool , HikariCP 등이 있다.
 * 성능과 사용의 편리함 측면에서 최근에는 hikariCP 를 주로 사용한다. 스프링 부트 2.0 부터는 기본 커넥션 풀로 hikariCP 를 제공한다.
 */

/**
 * DataSource란?
 * 예를 들어서 애플리케이션 로직에서 DriverManager 를 사용해서 커넥션을 획득하다가
 * HikariCP 같은 커넥션 풀을 사용하도록 변경하면 커넥션을 획득하는 애플리케이션 코드도 함께 변경해야 한다.
 * 의존관계가 DriverManager 에서 HikariCP 로 변경되기 때문이다.

 * 그래서 커넥션을 어떻게 획득할 건지 이 방법 또한 추상화해버렸다.
 * 자바에서는 이런 문제를 해결하기 위해 javax.sql.DataSource 라는 인터페이스를 제공한다.
 * DataSource 는 커넥션을 획득하는 방법을 추상화 하는 인터페이스이다.
 * 이 인터페이스의 핵심 기능은 커넥션 조회 하나이다.

 * public interface DataSource {
 *  Connection getConnection() throws SQLException;
 * }
 * 따라서 개발자는 DBCP2 커넥션 풀, HikariCP 커넥션 풀 의 코드를 직접 의존하는 것이 아니라
 * DataSource 인터페이스에만 의존하도록 애플리케이션 로직을 작성하면 된다.
 */