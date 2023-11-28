package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//ResponseStatusExceptionResolver 가 @ResponseStatus 어노테이션 확인 -> 클라이언트로 response.sendError(code, reason) , return ModelAndView();
//ResponseStatusExceptionResolver 가 reason 가져올 때 messageSource.getMessage 로 탐색하기 때문이다.
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.bad")
public class BadRequestException extends RuntimeException {

}
