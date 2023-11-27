package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//ResponseStatusExceptionResolver 가 @ResponseStatus 어노테이션 확인 -> response.sendError(code, reason) -> return ModelAndView();
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류")
public class BadRequestException extends RuntimeException {

}
