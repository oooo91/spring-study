package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

@Slf4j
public class AdvisorTest {

	@Test
	void advisorTest1() {
		ServiceImpl target = new ServiceImpl();
		ProxyFactory proxyFactory = new ProxyFactory(target);

		//어드바이저 만들기
		//Pointcut.TRUE -> 무조건 쓴다
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE,
			new TimeAdvice());
		proxyFactory.addAdvisor(advisor);
		ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

		proxy.save();
		proxy.find();

	}

	@Test
	@DisplayName("직접 만든 포인트컷")
	void advisorTest2() {
		ServiceImpl target = new ServiceImpl();
		ProxyFactory proxyFactory = new ProxyFactory(target);

		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointCut(),
			new TimeAdvice());
		proxyFactory.addAdvisor(advisor);
		ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

		proxy.save();
		proxy.find();

	}

	static class MyPointCut implements Pointcut {

		//클래스 확인
		@Override
		public ClassFilter getClassFilter() {
			return ClassFilter.TRUE; //언제나 true
		}

		//메서드 구현 확인
		@Override
		public MethodMatcher getMethodMatcher() {
			return new MyMethodMatcher();
		}
	}

	//직접 메서드 매처 구현
	static class MyMethodMatcher implements MethodMatcher {

		private String matchName = "save";

		//이것만 보면 돼
		@Override
		public boolean matches(Method method, Class<?> targetClass) {
			boolean result = method.getName().equals(matchName); //메서드 이름이 save 면 true 를 반환하도록
			log.info("포인트 컷 호출 method={} targetClass={}", method.getName(), targetClass);
			log.info("포인트 컷 결과 result={}", result);
			return result;
		}

		@Override
		public boolean isRuntime() {
			return false;
		}

		@Override
		public boolean matches(Method method, Class<?> targetClass, Object... args) {
			return false;
		}
	}


	@Test
	@DisplayName("스프링이 제공하는 포인트컷")
	void advisorTest3() {
		ServiceImpl target = new ServiceImpl();
		ProxyFactory proxyFactory = new ProxyFactory(target);

		//메서드 이름이 save 인 경우에만
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("save");

		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut,
			new TimeAdvice());
		proxyFactory.addAdvisor(advisor);
		ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

		proxy.save();
		proxy.find();

	}

}
