package hello.proxy.cglib;


import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

//인터페이스가 없는데도 동적 프록시를 만들 수 있다. (상속을 통해서)
@Slf4j
public class CglibTest {

	@Test
	void cglib() {

		//구체클래스를 기반으로 만들어야한다. 이 클래스를 받은 프록시를 만들어야한다! (chapter 4 참고)
		ConcreteService target = new ConcreteService();

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(ConcreteService.class);
		enhancer.setCallback(new TimeMethodInterceptor(target));
		ConcreteService proxy = (ConcreteService) enhancer.create();

		log.info("targetClass={}", target.getClass());
		log.info("proxyClass={}", proxy.getClass());

		proxy.call();
	}
}
