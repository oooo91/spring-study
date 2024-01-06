package hello.proxy.advisor;

import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class MultiAdvisorTest {

	@Test
	@DisplayName("여러 프록시")
	void multiAdvisorTest1() {
		//client -> proxy2(advisor2) -> proxy1(advisor1) -> target

		//프록시 1 생성
		ServiceImpl target = new ServiceImpl();
		ProxyFactory proxyFactory1 = new ProxyFactory(target);
		DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE,
			new Advice1());
		proxyFactory1.addAdvisor(advisor1);
		ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();


		//프록시 2 생성 (주의! target 을 넣으면 안 됨 프록시 -> 프록시로 가야하니까)
		ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
		DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE,
			new Advice2());
		proxyFactory2.addAdvisor(advisor2);
		ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

		proxy2.save();

	}

	//이걸 알려주는 이유 -> aop 적용 수 만큼 프록시가 생성되는 게 아니다. 하나의 target 에 여러 스프링 aop 가 적용되도, 프록시는 하나고 이 안에서 여러 어드바이저를 적용한다.
	@Test
	@DisplayName("하나의 프록시, 여러 어드바이저") //proxyFactory <- 1 : n -> target (위와 결과는 같고, 성능은 좋다)
	void multiAdvisorTest2() {
		//client -> proxy (-> advisor2 -> advisor1) -> target

		DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE,
			new Advice1());
		DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE,
			new Advice2());

		//프록시 1 생성
		ServiceImpl target = new ServiceImpl();
		ProxyFactory proxyFactory1 = new ProxyFactory(target);

		//순서 주의
		proxyFactory1.addAdvisor(advisor2);
		proxyFactory1.addAdvisor(advisor1);
		ServiceInterface proxy = (ServiceInterface) proxyFactory1.getProxy();

		proxy.save();

	}

	//static 으로 어드바이스 생성
	@Slf4j
	static class Advice1 implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			log.info("advice1 호출");
			return invocation.proceed();
		}
	}

	@Slf4j
	static class Advice2 implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			log.info("advice2 호출");
			return invocation.proceed();
		}
	}


}
