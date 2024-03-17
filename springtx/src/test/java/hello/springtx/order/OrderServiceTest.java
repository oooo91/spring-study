package hello.springtx.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OrderServiceTest {

	@Autowired OrderService orderService;
	@Autowired OrderRepository orderRepository;

	//커밋이 되
	@Test
	void order() throws NotEnoughMoneyException {
		//given
		Order order = new Order();
		order.setUsername("정상");

		//when
		orderService.order(order);

		//then
		Order findOrder = orderRepository.findById(order.getId()).get();
		assertThat(findOrder.getPayStatus()).isEqualTo("완료");
	}

	@Test
	void runtimeException() throws NotEnoughMoneyException {
		//given
		Order order = new Order();
		order.setUsername("예외");

		//when -> 롤백이 되기 때문에 empty 가 true 된다.
		assertThatThrownBy(() -> orderService.order(order)).isInstanceOf(RuntimeException.class);

		//then
		Optional<Order> orderOptional = orderRepository.findById(order.getId());
		assertThat(orderOptional.isEmpty()).isTrue();
	}

	//커밋이 돼
	@Test
	void bixException() {
		//given
		Order order = new Order();
		order.setUsername("잔고부족");

		//when -> 롤백이 되기 때문에 empty 가 true 된다.
		try {
			orderService.order(order);
		} catch (NotEnoughMoneyException e) {
			log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
		}

		//then
		Order findOrder = orderRepository.findById(order.getId()).get();
		assertThat(findOrder.getPayStatus()).isEqualTo("대기");
	}

}