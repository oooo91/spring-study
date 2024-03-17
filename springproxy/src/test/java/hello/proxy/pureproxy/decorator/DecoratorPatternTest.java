package hello.proxy.pureproxy.decorator;

import hello.proxy.pureproxy.decorator.code.Component;
import hello.proxy.pureproxy.decorator.code.DecoratorPatternClient;
import hello.proxy.pureproxy.decorator.code.MessageDecorator;
import hello.proxy.pureproxy.decorator.code.RealComponent;
import hello.proxy.pureproxy.decorator.code.TimeDecorator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DecoratorPatternTest {

	@Test
	void noDecorator() {
		RealComponent realComponent = new RealComponent();
		DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
		client.execute();
	}

	@Test
	void decorator1() {
		Component realComponent = new RealComponent();
		Component messageDecorator = new MessageDecorator(realComponent); //프록시에 실제 객체 주입
		DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
		client.execute();
	}

	//프록시 체인 -> 순서 중요 (클라이언트 -> time 프록시 -> message 프록시 -> real component 주입)
	@Test
	void decorator2() {
		Component realComponent = new RealComponent();
		Component messageDecorator = new MessageDecorator(realComponent);
		Component timeDecorator = new TimeDecorator(messageDecorator);
		DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
		client.execute();
	}
}
