package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloTraceV2 {

	private static final String START_PREFIX = "-->";
	private static final String COMPLETE_PREFIX = "<--";
	private static final String EX_PREFIX = "<X-";

	//로그 시작
	public TraceStatus begin(String message) {
		TraceId traceId = new TraceId();
		Long startTimeMs = System.currentTimeMillis();
		log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX,
			traceId.getLevel()), message);
		return new TraceStatus(traceId, startTimeMs, message);
	}

	//V2에서 추가 (같은 요청일 시 아이디 동기화)
	public TraceStatus beginSync(TraceId beforeTraceId, String message) {
		TraceId nextId = beforeTraceId.createNextId(); //레벨 증가
		Long startTimeMs = System.currentTimeMillis();
		log.info("[" + nextId.getId() + "] " + addSpace(START_PREFIX,
			nextId.getLevel()) + message);
		return new TraceStatus(nextId, startTimeMs, message);
	}

	//로그 종료
	public void end(TraceStatus status) {
		complete(status, null);
	}

	//예외가 터져서 로그가 종료될 시
	public void exception(TraceStatus status, Exception e) {
		complete(status, e);
	}

	private void complete(TraceStatus status, Exception e) {
		Long stopTimeMs = System.currentTimeMillis();
		long resultTimeMs = stopTimeMs - status.getStartTimeMs();
		TraceId traceId = status.getTraceId();
		if (e == null) {
			log.info("[{}] {}{} time={}ms", traceId.getId(),
				addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
				resultTimeMs);
		} else {
			log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
				addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs,
				e.toString());
		}
	}

	//level = 0
	//level = 1 |-->
	//level = 2 |	|-->

	//level = 2 ex |	|<X-
	//level = 1 ex |<X-

	private static String addSpace(String prefix, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append((i == level - 1) ? "|" + prefix : "| ");
		}
		return sb.toString();

	}
}
