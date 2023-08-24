package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ControllerV1 과 차이점 -> ControllerV2 는 MyView를 반환한다.
 * (/v2/controller 안의 모든 컨트롤러 -> jsp 이동하는 컨트롤이라)
 */
public interface ControllerV2 {
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
