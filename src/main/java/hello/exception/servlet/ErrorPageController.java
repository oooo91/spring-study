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

  //오류가 떨어져서 /error-page/500 일 때, 그리고 기존 요청이 api 간의 요청 (application/json)일 때
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















