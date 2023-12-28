package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public interface OrderControllerV1 {

	@GetMapping("/v1/request")
	String request(@RequestParam("itemId") String itemId); //인터페이스에서는 ("itemId") 이름을 제대로 명시해야한다, 명시 안 할 시 컴파일 때 문제가 생길 수 있다.

	@GetMapping("/v1/no-log")
	String noLog();
}
