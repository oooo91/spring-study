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

             * 데이터베이스에 연결하려면 JDBC가 제공하는 DriverManager.getConnection(..) 를 사용하면 된다.
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
