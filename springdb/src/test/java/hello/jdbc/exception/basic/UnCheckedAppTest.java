package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * 런타임 예외를 사용하면 중간에 기술이 변경되어도 해당 예외를 사용하지 않는 컨트롤러, 서비스에서는 코드를 변경하지 않아도 된다.
 * 구현 기술이 변경되는 경우, 예외를 공통으로 처리하는 곳에서는 예외에 따른 다른 처리가 필요할 수 있다.
 * 하지만 공통 처리하는 한곳만 변경하면 되기 때문에 변경의 영향 범위는 최소화 된다
 * 현재 스프링은 대부분 런타임 예외를 사용한다 (대신 문서화를 진심으로 해야한다)
 */
@Slf4j
public class UnCheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            /**
             * e.printStackTrace()
             * 얘 쓰면 System.out에 출력되므로 실무에서 쓰지 말자
             */
            e.printStackTrace();
            log.info("ex", e);
        }
    }


    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    /**
     * 체크 예외를 런타임 예외로 바꾸자! (의존성 없애기)
     */
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                /**
                 * RuntimeSQLException(e) <- e를 꼭 넣어줘야한다.
                 * 예외를 전환할 때는 꼭! 기존 예외를 포함해야 한다.
                 * 안 넣으면 실제 예외를 확인할 수 없다.
                 */
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }


}
