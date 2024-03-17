package hello.advanced.trace.template;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;

public abstract class AbstractTemplate<T> {

	private final LogTrace trace;

	protected AbstractTemplate(LogTrace trace) {
		this.trace = trace;
	}

	/**
	 * 반환 타입이 조금씩 다르다 -> 제네릭 타입
	 * 객체를 생성할 때 타입을 줄 수 있는데, 그떄 execute 에 해당하는 타입도 그 타입에 맞춰진다.
	 */
	public T execute(String message) {
		TraceStatus status = null;

		try {
			status = trace.begin(message);
			
			//비즈니스 로직 호출
			T result = call();

			trace.end(status);
			return result;
		} catch (Exception e) {
			trace.exception(status, e);
			throw e;
		}
	}

	protected abstract T call();
}
