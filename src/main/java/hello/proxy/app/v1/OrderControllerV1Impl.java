package hello.proxy.app.v1;

public class OrderControllerV1Impl implements OrderControllerV1 {

	private final OrderServiceV1 orderService;

	public OrderControllerV1Impl(OrderServiceV1 orderServiceV1) {
		this.orderService = orderServiceV1;
	}

	//LogTrace 적용
	@Override
	public String request(String itemId) {
		orderService.orderItem(itemId);
		return "ok";
	}

	//LogTrace 적용 안함
	@Override
	public String noLog() {
		return "ok";
	}
}
