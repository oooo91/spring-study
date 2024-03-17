package hello.advanced.app.v2;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {

	private final OrderServiceV2 orderService;
	private final HelloTraceV2 trace;

	/**
	 * trace id 를 parameter 로 넘겨서 동기화하는 문제점
	 * 로그 하나 찍으려고 모든 코드를 고쳐야 한다;;;;;;
	 * 인터페이스를 넣는 것도 좀... 인터페이스를 의존하는 코드도 다 고쳐야한다.
	 * 만약 컨트롤러에서 서비스를 호출하지 않고 다른 곳에서부터 호출한다면 넘길 trace id 도 없는 문제점이 발생한다.
	 * trace id 를 파라미터로 계속 넘기는 방법 말고 더 나은 대안은 없을까?
	 */

	@GetMapping("/v2/request")
	public String request(String itemId) {

		TraceStatus status = null;
		try {
			status = trace.begin("OrderController.request()");
			orderService.orderItem(status.getTraceId(), itemId); //이제 아이디를 넘긴다. 동기화하자.
			trace.end(status);
			return "ok";
		} catch (Exception e) {
			trace.exception(status, e);
			throw e; //예외를 꼭 다시 던져주어야 한다.
		}
	}
}
