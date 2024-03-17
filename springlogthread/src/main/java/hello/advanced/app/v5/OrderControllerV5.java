package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceCallback;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderControllerV5 {

	private final OrderServiceV5 orderService;
	private final TraceTemplate template; //template 은 변하지 않는 코드라 싱글톤으로 한 번 만들어만 두기

	//trace 도 파라미터로 주입하고
	@Autowired
	public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
		this.orderService = orderService;
		this.template = new TraceTemplate(trace);
	}

	//new TraceCallback 이라는 콜백도 파라미터로 주입한다.
	@GetMapping("/v5/request")
	public String request(String itemId) {
		return template.execute("OrderControllerV5.request()", new TraceCallback<>() {
			@Override
			public String call() {
				orderService.orderItem(itemId);
				return "ok";
			}
		});
	}
}
