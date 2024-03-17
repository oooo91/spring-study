package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

//프록시를 만들자
public class OrderServiceConcreteProxy extends OrderServiceV2 {

	//기본 생성자가 아니라 특별한 생성자가 있으면
	private final OrderServiceV2 target;
	private final LogTrace logTrace;

	public OrderServiceConcreteProxy(OrderServiceV2 target, LogTrace logTrace) {
		//상속이면 부모 기본 생성자가 필요하다. 분법상 super 를 강제로 호출해야하므로 어쩔 수 없이 super(null) 로 한다.
		super(null);
		this.target = target;
		this.logTrace = logTrace;
	}

	@Override
	public void orderItem(String itemId) {
		TraceStatus status = null;
		try {
			status = logTrace.begin("OrderService.orderItem()");
			//target 호출
			target.orderItem(itemId);
			logTrace.end(status);
		} catch (Exception e) {
			logTrace.exception(status, e);
			throw e;
		}
	}
}
