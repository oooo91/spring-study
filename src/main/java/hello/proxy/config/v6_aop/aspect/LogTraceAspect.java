package hello.proxy.config.v6_aop.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class LogTraceAspect {

	private final LogTrace logTrace;

	public LogTraceAspect(LogTrace logTrace) {
		this.logTrace = logTrace;
	}

	//이게 어드바이저
	@Around("execution(* hello.proxy.app..*(..))") //포인트 컷 조건
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		//여기 어드바이스 (부가 기능)
		TraceStatus status = null;
		try {
			//Method method = invocation.getMethod();
			//String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
			String message = joinPoint.getSignature().toShortString();

			status = logTrace.begin(message);

			Object result = joinPoint.proceed(); //target 호출
			logTrace.end(status);

			return result;

		} catch (Exception e) {

			logTrace.exception(status, e);
			throw e;
		}
	}
}
