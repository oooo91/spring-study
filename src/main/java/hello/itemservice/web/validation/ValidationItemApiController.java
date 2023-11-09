package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

  //HttpMessageConverter가 @RequestBody 보고 Json 파싱해서 ItemSaveForm 으로 만들게 <- 안 되면 컨트롤러 호출도 안 되고 검증도 안 된다. (객체 단위)
  //@ModelAttribute는 이와 다른데 필드 하나씩 조사하기 때문에 타입에 맞지 않는 필드가 있더라도 다른 필드는 정상 처리될 수 있다. (필드 단위)
  @PostMapping("/add")
  public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {
    log.info("API 컨트롤러 호출");

    if (bindingResult.hasErrors()) {
      log.info("검증 오류 발생 error={}" , bindingResult);
      return bindingResult.getAllErrors();
    }
    log.info("성공 로직 실행");
    return form;
  }
}
