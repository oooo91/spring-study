package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

@Slf4j
public class ProxyFactoryTest {

	@Test
	@DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
	void interfaceProxy() {
		ServiceImpl target = new ServiceImpl();

		//프록시 팩토리를 통해 프록시를 만들자.
		ProxyFactory factory = new ProxyFactory(target); //얘가 알아서 프록시 틀 만들어준다.
		factory.addAdvice(new TimeAdvice()); //걍 넣으면 끝나네

		ServiceInterface proxy = (ServiceInterface) factory.getProxy();
		log.info("targetClass={}", target.getClass());
		log.info("proxyClass={}", proxy.getClass());

		proxy.save();

		//프록시 팩토리 한에서 만들어진 것만 테스트 가능, 직접 jdk 프록시 만든 경우는 스프링의 AopUtils 사용 불가능
		Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();
		Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
	}

	@Test
	@DisplayName("구체 클래스만 있으면 CGLIB 사용")
	void concreteProxy() {
		ConcreteService target = new ConcreteService();

		ProxyFactory factory = new ProxyFactory(target);
		factory.addAdvice(new TimeAdvice());

		ConcreteService proxy = (ConcreteService) factory.getProxy();
		log.info("targetClass={}", target.getClass());
		log.info("proxyClass={}", proxy.getClass());

		proxy.call();

		Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();
		Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
	}

	@Test
	@DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB 를 사용하고, 클래스 기반 프록시를 사용할 수 있다.")
	void proxyTargetClass() {
		ServiceImpl target = new ServiceImpl();

		ProxyFactory factory = new ProxyFactory(target);
		factory.setProxyTargetClass(true); //인터페이스더라도 난 항상 cglib 기반의 프록시를 사용할 거야! (targetClass (ServiceImpl이 클래스) 를 기반으로 만들어버리므로)
		factory.addAdvice(new TimeAdvice());

		ServiceInterface proxy = (ServiceInterface) factory.getProxy();
		log.info("targetClass={}", target.getClass());
		log.info("proxyClass={}", proxy.getClass());

		proxy.save();

		Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();
		Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
	}
}
