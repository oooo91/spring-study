package hello.exception.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class ErrorPageController {

  //EXCEPTION 이 터져서 WAS 까지 도달할 때 WAS 가 request.setAttribute 로 아래의 필드를 정의한다.
  public static final String ERROR_EXCEPTION = "jakarta.servlet.error.exception";
  public static final String ERROR_EXCEPTION_TYPE = "jakarta.servlet.error.exception_type";
  public static final String ERROR_MESSAGE = "jakarta.servlet.error.message";
  public static final String ERROR_REQUEST_URI = "jakarta.servlet.error.request_uri";
  public static final String ERROR_SERVLET_NAME = "jakarta.servlet.error.servlet_name";
  public static final String ERROR_STATUS_CODE = "jakarta.servlet.error.status_code";

  //오류 페이지 작동 원리
  //오류 발생 -> WAS 까지 도달 -> WAS 가 ERROR PAGE 까봄 -> 오류에 매핑된 PAGE 가 있다면 WAS 가 오류 페이지를 요청 (서버 내부에서 REQUEST 됨)(컨트롤러가 총 두 번 요청)
  //두 번째 요청할 때 이때 요청만 하는 것이 아니라 오류 정보를 REQUEST 의 ATTRIBUTE 에 저장해서 넘긴다. 필요하면 오류 페이지에서 이렇게 전달된 오류 정보를 사용할 수 있다.
  //어쨌거나 중요한 점은 클라이언트는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다. 서버 내부에서만 추가적인 요청을 호출한다.
  @RequestMapping("/error-page/404")
  public String errorPge404(HttpServletRequest request, HttpServletResponse response) {
    log.info("errorPage 404");
    printErrorInfo(request);
    return "error-page/404";
  }

  @RequestMapping("/error-page/500")
  public String errorPge500(HttpServletRequest request, HttpServletResponse response) {
    log.info("errorPage 500");
    printErrorInfo(request);
    return "error-page/500";
  }

  //기존 요청이 api 간의 요청 (application/json)일 때는 스프링 부트(BasicController)가 제공하는 오류 메커니즘으로 api 오류를 처리할 수 있다.
  //앞서 본 BasicErrorController 는
  //예외가 터져서 -> WAS 까지 도달하면 -> 스프링 부트가 기본 ERROR 페이지를 등록한 다음 (/error 로 시작), BasicErrorController 를 호출한다.
  //BasicController 을 까보면 text/html 로 요청이 오면 text/html 를 (/error/** html 반환) json 요청이 오면 json 형식으로 응답하도록 설계되어있다.
  //기본적인 json 응답 결과 스펙은 timestamp, status, error, exception, path 를 포함한다.
  //단, 응답 결과 스펙이 api 요청마다 다를 수 있다. 예를 들어 상품 관련 예외, 회원 관련 예외 시 같은 400 이라도 예외를 다르게 처리해야하는 상황이라면 이 방법은 좋지 않다.
  //BasicErrorController 를 확장해서 JSON 형식 메시지를 변경할 수 있다 정도로만 이해하고, API 오류 처리는 @ExceptionHandler 를 사용하는 것이 좋다.
  @RequestMapping(value = "/error-page/500", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> errorPage500Api(HttpServletRequest request,
      HttpServletResponse response) {
    log.info("API errorPage 500");

    Map<String, Object> result = new HashMap<>();
    Exception ex = (Exception) request.getAttribute(ERROR_EXCEPTION);
    result.put("status", request.getAttribute(ERROR_STATUS_CODE));
    result.put("message", ex.getMessage());
    Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));
  }

  //그저 오류 페이지에 도달할 것이 아니라 오류 정보 뜯어보기
  //첫 글자들 선택 : shift + 아래 화살
  //다 선택 : ctrl + shift + 오른쪽 화살
  private void printErrorInfo(HttpServletRequest request) {
    log.info("ERROR_EXCEPTION: {}", request.getParameter("ERROR_EXCEPTION"));
    log.info("ERROR_EXCEPTION_TYPE: {}", request.getParameter("ERROR_EXCEPTION_TYPE")); //dispatcherType=ERROR
    log.info("ERROR_MESSAGE: {}", request.getParameter("ERROR_MESSAGE"));
    log.info("ERROR_REQUEST_URI: {}", request.getParameter("ERROR_REQUEST_URI"));
    log.info("ERROR_SERVLET_NAME: {}", request.getParameter("ERROR_SERVLET_NAME"));
    log.info("ERROR_STATUS_CODE: {}", request.getParameter("ERROR_STATUS_CODE"));
  }

}















