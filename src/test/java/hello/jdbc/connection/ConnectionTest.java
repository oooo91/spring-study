package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static hello.jdbc.connection.ConnectionConst.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        /**
         * 각각 데이터에비스에서 다른 커넥션을 가져오게 된다.
         * 먼저 기존에 개발했던 DriverManager 를 통해서 커넥션을 획득하는 방법을 확인해보자.
         */
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());

    }

    /**
     * 이번에는 스프링이 제공하는 DataSource 가 적용된 DriverManager 인 DriverManagerDataSource 를 사용해보자
     * 기존 코드와 비슷하지만 DriverManagerDataSource 는 DataSource 를 통해서 커넥션을 획득할 수 있다.
     * 참고로 DriverManagerDataSource 는 스프링이 제공하는 코드이다.
     *
     * 기존 DriverManager와 DriverManagerDataSource 차이
     * DriverManager 는 커넥션을 획득할 때 마다 URL , USERNAME , PASSWORD 같은 파라미터를 계속 전달해야 한다.
     * 반면에 DataSource 를 사용하는 방식은 처음 객체를 생성할 때만 필요한 파리미터를 넘겨두고,
     * 커넥션을 획득할 때는 단순히 dataSource.getConnection() 만 호출하면 된다.
     * 따라서 URL , USERNAME , PASSWORD 같은 속성들에 의존하지 않아도 된다.
     */
    @Test
    void dataSourceDriverManager() throws SQLException {
        //DriverManageDataSource - 항상 새로운 커넥션을 획득
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    /**
     * HikariCP 커넥션 풀을 사용한다. HikariDataSource 는 DataSource 인터페이스를 구현하고 있다.
     * 커넥션 풀 최대 사이즈를 10으로 지정하고, 풀의 이름을 MyPool 이라고 지정했다.
     * 커넥션 풀에서 커넥션을 생성하는 작업은 애플리케이션 실행 속도에 영향을 주지 않기 위해 별도의 쓰레드에서 작동한다.
     * 별도의 쓰레드에서 동작하기 때문에 테스트가 먼저 종료되어 버린다.
     * 아래처럼 Thread.sleep 을 통해 대기 시간을 주어야 쓰레드 풀에 커넥션이 생성되는 로그를 확인할 수 있다.
     * 커넥션 풀에서 커넥션 2개 이상 획득하면 -> 대기 상태인 풀이 8개, 실행상태 2개인 스레드 출력된다. (con1, con2)
     * 그리고 10개 다 쓰면 풀이 확보될 때까지 대기(락) 상태가 된다. (Hikari에서 대기 시간을 설정할 수 있다.)
     */
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링, 스프링 부트가 알아서 외부 라이브러리(Hikari) import함
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }


    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection(); //여기서 커넥션이 1개 이상이어야 출력 (없으면 알아서 좀 대기한다.)
        Connection con2 = dataSource.getConnection(); //여기서 커넥션이 2개 이상이어야 출력
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
