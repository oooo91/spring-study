package hello.advanced.trace.logtrace;

import static org.junit.jupiter.api.Assertions.*;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;

class FieldLogTraceTest {

	FieldLogTrace trace = new FieldLogTrace();

	/**
	 * 일단은 holder 에 traceId 하나만 가질 수 있다. (아직 이거 구분 안 지었다.)
	 * trace.begin("hello2") 은 traceHolder 에 traceId 가 이미 있기 때문에 trace.begin("hello1") 의 traceId 를 이어 받는다.
	 * trace.end(status2) 여기서도 레벨이 1이기 때문에 바로 종료가 되지 않는다.
	 * trace.end(status1) 이 되어야 비로소 종료된다.
	 *
	 * hello1
	 * |-->hello2
	 * |<--hello2
	 * hello1
	 */

	@Test
	void begin_end_level2() {
		TraceStatus status1 = trace.begin("hello1");
		TraceStatus status2 = trace.begin("hello2");
		trace.end(status2);
		trace.end(status1);
	}

	/**
	 * hello1
	 * |-->hello2
	 * |<X-hello2
	 * hello1
	 */
	@Test
	void begin_exception_level2() {
		TraceStatus status1 = trace.begin("hello1");
		TraceStatus status2 = trace.begin("hello2");
		trace.exception(status2, new IllegalStateException());
		trace.exception(status1, new IllegalStateException());
	}
}