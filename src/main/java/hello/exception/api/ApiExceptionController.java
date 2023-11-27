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
    if (id.equals("ex")) {
      //api 간의 통신이므로 application/json 형식으로 데이터를 주고 받기 때문에 500일 시 html 코드 자체가 떨어진다. 이러면 안 된다.
      throw new RuntimeException("잘못된 사용자");
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
