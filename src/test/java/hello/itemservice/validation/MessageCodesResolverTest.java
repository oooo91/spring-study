package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

public class MessageCodesResolverTest {
  MessageCodesResolver codesResolver = new DefaultMessageCodesResolver(); //ex required -> 여러 required 에러 반환

  @Test
  void messageCodesResolverObject() {
    String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
    for (String messageCode : messageCodes) { //단축키 iter
      System.out.println("messageCode = " + messageCode);
    }
    Assertions.assertThat(messageCodes).containsExactly("required.item", "required");
    /*
     bindingResult.rejectValue("itemName", "required") -> 내부에 resolver 동작, required.itemName, required 를 만든다. 우선순위 높은 것부터 찾는다.
     fieldError 에서의 파라미터 code 역시 resolver 에서 만든 코드들이다.
     */
  }

  @Test
  void messageCodesResolverField() {
    String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName",
        String.class);
    for (String messageCode : messageCodes) {
      System.out.println("messageCode = " + messageCode);
    }
    Assertions.assertThat(messageCodes).containsExactly("required.item.itemName",
                                                                "required.item",
                                                                "required.java.lang.String",
                                                                "required") ;
  }
}
