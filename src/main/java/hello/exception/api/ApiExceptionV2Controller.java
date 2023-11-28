package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

  //ExceptionHandler 너무 복잡다. @ExceptionHandler 사용하여 exceptionHandlerExceptionResolver 를 활용하자.
  //해당 컨트롤러에서 IllegalArgumentException 예외가 발생하면 ExceptionHandler 가 잡고,
  //ExceptionHandler 가 ErrorResult 를 json 형태로 반환한다. (httpMessageConverter) (BasicErrorController 의 기본 리턴 형태로 반환 안 된다!)
  //컨트롤 예외 -> resolver 에서 예외 해결 시도 -> exceptionHandlerExceptionResolver (가장 우선순위 높음) 가 실행 -> @ExceptionHandler 어노테이션 있으면 호출
  //근데 문제는 예외를 잘 잡아서 정상 동작되어 WAS 에서 200 으로 인지한다. -> @ResponseStatus 어노테이션을 사용하여 예외 상태 코드를 바꾸자
  //드디어 서블릿 컨테이너까지 예외 전파 안 되고 정상 흐름으로 동작하되, 예외를 던질 수 있게 되었다!
  //공식 문서를 보면 return 가능한 value 가 @ResponseBody, HttpEntity, ResponseEntity, String, View 등이 있다.
  //String -> viewResolver 로 가서 view 를 반환한다.
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ErrorResult illegalExHandler(IllegalArgumentException e) {
    log.error("[exceptionHandler] ex", e);
    return new ErrorResult("BAD", e.getMessage());
  }

  //다른 방법
  //@ExceptionHandler(UserException.class) 과 같다.
  @ExceptionHandler
  private ResponseEntity<ErrorResult> userExHandler(UserException e) {
    log.error("[exceptionHandler] ex", e);
    ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
    return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
  }

  //위의 IllegalArgumentException 예외나 UserException 가 아닌 것들은 여기서 다 잡는다.
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler
  public ErrorResult exHandler(Exception e) {
    log.error("[exceptionHandler] ex", e);
    return new ErrorResult("Ex", "내부 오류");
  }

  @GetMapping("/api2/members/{id}")
  private MemberDto getMember(@PathVariable("id") String id) {

    if (id.equals("ex")) {
      throw new RuntimeException("잘못된 사용자");
    }

    if (id.equals("bad")) {
      throw new IllegalArgumentException("잘못된 입력 값");
    }

    if (id.equals("user-ex")) {
      throw new UserException("사용자 오류");
    }

    return new MemberDto(id, "hello " + id);
  }

  @Data
  @AllArgsConstructor
  static class MemberDto {

    private String memberId;
    private String name;
  }

}
