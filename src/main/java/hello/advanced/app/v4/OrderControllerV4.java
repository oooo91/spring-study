package hello.advanced.app.v4;

import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV4 {

	private final OrderServiceV4 orderService;
	private final LogTrace trace;

	/** 기존 아래의 코드를 -> 템플릿 뼤놓자,
	 * 그리고 만든 템플릿을 사용하자
	 * try {
	 * 			status = trace.begin("OrderController.request()");
	 * 			orderService.orderItem(itemId); //이제 파라미터로 id 를 넘기지말고 begin 이 시작하면 holder 를 사용할 것이다.
	 * 			trace.end(status);
	 * 			return "ok";
	 *                } catch (Exception e) {
	 * 			trace.exception(status, e);
	 * 			throw e; //예외를 꼭 다시 던져주어야 한다.
	 *        }
	 * **/
	@GetMapping("/v4/request")
	public String request(String itemId) {

		AbstractTemplate<String> template = new AbstractTemplate<String>(trace) {
			@Override
			protected String call() {
				orderService.orderItem(itemId);
				return "ok";
			}
		};
		return template.execute("OrderControllerV4.request()");
	}
}
