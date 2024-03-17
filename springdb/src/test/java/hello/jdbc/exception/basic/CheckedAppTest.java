package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 * 그지같은 체크 예외 TEST -> 무조건 처리해야하는 예외
 * 처리할 수 있는 체크 예외라면 서비스나 컨트롤러에서 처리하겠지만, 지금처럼 데이터베이스나 네트워크 통신처럼 시스템 레벨에서 올라온 예외들은 대부분 복구가 불가능하다.
 * 그리고 실무에서 발생하는 대부분의 예외들은 이런 시스템 예외들이다.
 * 문제는 이런 경우에 체크 예외를 사용하면 아래에서 올라온 복구 불가능한 예외를 서비스, 컨트롤러 같은 각각의 클래스가 모두 알고 있어야 한다.
 * 그래서 불필요한 의존관계 문제가 발생하게 된다.

 * throws Exception
 * SQLException , ConnectException 같은 시스템 예외는 컨트롤러나 서비스에서는 대부분 복구가 불가능하고 처리할 수 없는 체크 예외이다.
 * 따라서 다음과 같이 처리해주어야 한다.
 * void method() throws SQLException, ConnectException {..}

 * 그런데 다음과 같이 최상위 예외인 Exception 을 던져도 문제를 해결할 수 있다.
 * void method() throws Exception {..}
 * 이렇게 하면 Exception 은 물론이고 그 하위 타입인 SQLException , ConnectException 도 함께 던지게 된다.
 * 코드가 깔끔해지는 것 같지만, Exception 은 최상위 타입이므로 모든 체크 예외를 다 밖으로 던지는 문제가 발생한다.
 */
public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }
}
