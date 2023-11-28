package hello.exception.api;

import hello.exception.exception.BadRequestException;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

  @GetMapping("/api/members/{id}")
  private MemberDto getMember(@PathVariable("id") String id) {

    //WevServerCustomizer 를 사용할 적에는 application/json 통신이라고 html 코드 자체가 떨어진다.
    //api 간의 통신이므로 text/html 이 아니라 json 형식으로 에러 코드를 전송해야한다.
    //그러기 위해 WebServerCustomizer 를 주석 처리하고 스프링 부트가 기본적으로 제공하는 BasicErrorController 를 사용하자.
    //BasicErrorController 는 json 으로 요청이 오면 json 으로 에러 코드를 처리하기 때문이다.
    if (id.equals("ex")) {
      throw new RuntimeException("잘못된 사용자");
    }

    //그런데 api 별 예외 처리를 다르게 하고 싶다면?
    //잘못된 입력 값으로 서버에서 에러가 나면 -> 500 (IllegalArgumentException) 이 터진다. 사실상 에러는 500이다.
    //그러나 클라이언트에게 400 에러를 떨구고 싶다면, 어디서 예외를 바꿀 수 있을까 -> HandlerExceptionResolver : 예외 발생 시 동작을 새로 정의한다.
    if (id.equals("bad")) {
      throw new IllegalArgumentException("잘못된 입력 값");
    }

    //또한 기존에 사용한 BasicErrorController -> 서블릿 컨테이너까지 예외(exception)가 전달되었으나,
    //ExceptionResolver 를 활용하면 resolver 바로 예외를 처리할 수 있다. (exception -> sendError) (따로 뷰(ModelAndView)가 없다면 WAS 에서 오류 코드에 해당하는 /error 호출한다)
    if (id.equals("user-ex")) {
      throw new UserException("사용자 오류");
    }

    return new MemberDto(id, "hello " + id);
  }

  //스프링 부트 -> 여러 exceptionResolver 제공

  //ResponseStatusExceptionResolver -> http 응답 코드 변경
  //ResponseStatusExceptionResolver 가 @ResponseStatus 어노테이션 확인 -> response.sendError(code, reason) -> return ModelAndView();
  @GetMapping("/api/response-status-ex1")
  public String responseStatusEx1() {
    throw new BadRequestException();
  }

  //우리가 만든 예외가 아닌 우리가 수정할 수 없는 시스템 예외, 라이브러리 예외에는 @ResponseStatus 적용하기 어렵다.
  //추가로 어노테이션을 사용하기 떄문에 조건에 따라 동적으로 변경하는 것도 어렵다.
  //-> 이럴 때는 실제 RuntimeException 인 ResponseStatusException 을 터뜨리면 된다. (내가 넣을 오류 코드, 오류 메시지, 실제 예외)
  //이 예외도 ResponseStatusExceptionResolver 얘가 처리한다.
  @GetMapping("/api/response-status-ex2")
  public String responseStatusEx2() {
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad", new IllegalArgumentException());
  }

  //DefaultHandlerExceptionResolver -> 스프링 내부 예외 처리
  //DefaultHandlerExceptionResolver -> 얘가 알아서 클라이언트의 잘못은 500 에서 400 으로 변경해준다.
  //가령 클라이언트에서 String 타입의 data 를 보냈다면 사실 클라이언트 잘못이다. 그런데 서버에서 문제가 발생했기 때문에 어쨌거나 스프링은 500을 터뜨린다.
  //다만 defaultHandlerExceptionResolver 가 해당 오류를 보고 (TypeMismatchException) 이 오류에 관한 코드를 처리한다 (500 -> 400 변경)
  //이러한 수많은 정의들이 defaultHandlerExceptionResolver 에 있다.
  @GetMapping("/api/default-handler-ex")
  public String defaultException(@RequestParam Integer data) {
    return "ok";
  }

  @Data
  @AllArgsConstructor
  static class MemberDto {

    private String memberId;
    private String name;
  }
}
