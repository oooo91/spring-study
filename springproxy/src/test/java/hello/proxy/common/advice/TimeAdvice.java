package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

//로직 전후로 실행하는 프록시
@Slf4j
public class TimeAdvice implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		//아까 직접 MethodInterceptor 구현한 거랑 달리 target 이 필요없다.
		//프록시 팩토리를 만들 때 그쪽에서 이미 target 을 넣어줄 거다. => 이러한 정보들이 다 invocation 들어간다. target 을 찾아서 필요한 인수 다 넣고 실행시켜줄 애다.

		log.info("TimeProxy 실행");
		long startTime = System.currentTimeMillis();

		Object result = invocation.proceed();

		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;

		log.info("TimeProxy 종료 resultTime={}", resultTime);

		return result;
	}
}
