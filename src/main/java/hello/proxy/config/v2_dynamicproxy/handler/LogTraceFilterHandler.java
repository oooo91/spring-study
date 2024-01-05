package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.springframework.util.PatternMatchUtils;

//동적 프록시
public class LogTraceFilterHandler implements InvocationHandler {

	private final Object target; //실제 구현체
	private final LogTrace logTrace; //실제 구현체에 필요한 로직
	private final String[] patterns;

	public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
		this.target = target;
		this.logTrace = logTrace;
		this.patterns = patterns;
	}

	//직전 test 에는 proxy.call()<을 불렀다. method 는 call 의 정보를 받는다. args 는 method 의 인자 정보를 받는다.
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		//메서드 이름 필터
		String methodName = method.getName();
		//save, request, reque*, *est => 패턴 매칭안되면
		if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
			return method.invoke(target, args);
		}

		TraceStatus status = null;
		try {
			//메서드를 선언한 클래스를 가져온다
			String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
			status = logTrace.begin(message);
			//target 호출
			Object result = method.invoke(target, args); //String result = target.request(itemId);
			logTrace.end(status);
			return result;
		} catch (Exception e) {
			logTrace.exception(status, e);
			throw e;
		}
	}
}
