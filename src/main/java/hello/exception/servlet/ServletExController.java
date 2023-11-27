package hello.exception.servlet;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ServletExController {

  //예외 터짐 -> 오류 페이지 출력
  @GetMapping("/error-ex")
  public void errorEx() {
    throw new RuntimeException("예외 발생!");
  }

  //sendError -> exception 떨어지진 않는데 메서드 자체가 throws IOException 을 구현하고 있어서 상속 받아야한다.
  //WAS 가 sendError 까보고 호출되어있다면 오류 코드에 맞춘 페이지를 출력한다.
  @GetMapping("/error-404")
  public void error404(HttpServletResponse response) throws IOException {
    response.sendError(404, "404 오류!");
  }

  @GetMapping("/error-500")
  public void error500(HttpServletResponse response) throws IOException {
    response.sendError(500);
  }
}
