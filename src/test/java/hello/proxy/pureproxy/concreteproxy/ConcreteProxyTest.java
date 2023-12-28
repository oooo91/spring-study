package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.Test;

public class ConcreteProxyTest {

	/**
	 * concreteLogic 이거 인터페이스 없고 구체 클래스인데도 프록시를 도입할 수 있다.
	 * 클래스 기반 프록시 도입 -> 상속을 사용한다.
	 */
	@Test
	void noProxy() {
		ConcreteLogic concreteLogic = new ConcreteLogic();
		ConcreteClient concreteClient = new ConcreteClient(concreteLogic);
		concreteClient.execute();
	}

	@Test
	void addProxy() {
		ConcreteLogic concreteLogic = new ConcreteLogic();
		TimeProxy timeProxy = new TimeProxy(concreteLogic); //추가
		ConcreteClient client = new ConcreteClient(timeProxy); //주입 변경 -> 가능한 이유는 ConcreteLogic 의 자식이 TimeProxy 이잖아요
		client.execute();
	}

}
