package hello.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiExceptionController {

  @GetMapping("/api/members/{id}")
  private MemberDto getMember(@PathVariable("id") String id) {

    //WevServerCustomizer 를 사용할 적에는 application/json 통신이라고 html 코드 자체가 떨어진다.
    //api 간의 통신이므로 text/html 이 아니라 json 형식으로 에러 코드를 전송해야한다.
    //그러기 위해 WevServerCustomizer 를 주석 처리하고 스프링 부트가 기본적으로 제공하는 BasicErrorController 를 사용하자.
    //BasicErrorController 는 json 으로 요청이 오면 json 으로 에러 코드를 처리하기 때문이다.
    if (id.equals("ex")) {
      throw new RuntimeException("잘못된 사용자");
    }

    //그런데 api 별 예외처리를 다르게 하고 싶다면?
    //잘못된 입력 값으로 서버에서 에러가 나면 -> 500 (IllegalArgumentException) 이 터진다. 사실상 에러는 500이다.
    //그러나 클라이언트에게 400 에러를 떨구고 싶다면, 어디서 예외를 바꿀 수 있을까 -> HandlerExceptionResolver
    //컨트롤러예서 예외 발생 -> 디스패처 서블릿 -> postHandle 호출 x , 대신 이때 ExceptionResolver 실행하여 예외를 해결하도록 시도한다 -> WAS 에 정상 응답한다.
    if (id.equals("bad")) {
      throw new IllegalArgumentException("잘못된 입력 값");
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
