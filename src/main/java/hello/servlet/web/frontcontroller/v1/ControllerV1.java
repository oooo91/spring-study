package hello.servlet.web.frontcontroller.v1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 컨트롤러 인터페이스 도입
 * 각 컨트롤러들은 이 인터페이스를 구현 -> 프론트 컨트롤러(서블릿)가 이 인터페이스를 호출하여 구현과 관계없이 로직의 일관성을 가질 수 있게 함
 */
public interface ControllerV1 {
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
