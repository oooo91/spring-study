package hello.jdbc.connection;

import com.zaxxer.hikari.util.DriverDataSource;
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

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
