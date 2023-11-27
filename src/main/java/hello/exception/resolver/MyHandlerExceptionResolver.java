package hello.exception.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

  @Override
  public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {

    try {
      //예외가 터지면 DispatcherServlet 이 ExceptionResolver 에게 해결 가능한지 물어본다.
      //이때 ExceptionHandler 가 ModelAndView 를 반환하면 정상 처리한다.
      //ModelAndView 비어있으니까 뷰는 호출하지 않고, WAS 에서 sendError 까본 다음 오류 페이지를 찾는다. (exception -> sendError 로 바꾸기)
      //이와 같이 예외 상태 코드를 변환하는 것뿐만 아니라
      //뷰 템플릿 처리 (이때는 ModelAndView 가 null 이 아니다) 또는 API 응답 처리 (JSON, HTTP 바디에 직접 데이터 넣기) 도 가능하다.
      //또한 WAS 까지 도달하지 않고도, 왔다갔다 안 하고도 ExceptionResolver 선에서 처리할 수 있다.
      if (ex instanceof IllegalArgumentException) {
        log.info("IllegalArgumentException resolver t0 400");
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        return new ModelAndView();
      }
    } catch (IOException e) {
      log.error("resolver ex", e);
    }
    return null;
  }
}
