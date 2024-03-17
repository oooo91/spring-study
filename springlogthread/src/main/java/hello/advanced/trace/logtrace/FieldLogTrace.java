package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldLogTrace implements LogTrace {

	private static final String START_PREFIX = "-->";
	private static final String COMPLETE_PREFIX = "<--";
	private static final String EX_PREFIX = "<X-";

	/**
	 * 파라미터로 trace id 동기화 안하니까
	 * 어디든 TraceId 를 보관해야할 텐데 (다음으로 넘기기 위해)
	 * 우리는 traceIdHolder 라는 곳으로 보관할 것이다.
	 */
	private TraceId traceIdHolder; //traceId 동기화, 동시성 이슈 발생

	/**
	 * 동시성 이슈
	 * 예를 들어 1초 안에 요청을 두 번 했다고 가정하자.
	 * 그럼 스레드 풀에서 스레드 2개를 사용하여 FieldLogTrace 를 사용할 텐데
	 * 문제는 FieldLogTrace 가 싱글톤이라서 인스턴스가 어플리케이션에 딱 1 개 존재한다.
	 * 따라서 이 두 스레드가 같은 FieldLogTrace 자원을 공유하다보니, traceId 도 동일해지고 순서도 뒤죽박죽이 돼버리는 이슈가 발생한다.
	 */



	@Override
	public TraceStatus begin(String message) {
		//TraceId traceId = new TraceId();
		syncTraceId();

		TraceId traceId = traceIdHolder; //begin 시작하면 traceHolder 에 traceId 가 반드시 보관될 거니까 여기서 꺼내기 (securitycontextholder 느낌이다)

		Long startTimeMs = System.currentTimeMillis();
		log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX,
			traceId.getLevel()), message);
		return new TraceStatus(traceId, startTimeMs, message);
	}

	//level + 1 씩 증가
	private void syncTraceId() {
		if (traceIdHolder == null) {
			traceIdHolder = new TraceId();
		} else {
			traceIdHolder = traceIdHolder.createNextId();
		}
	}

	@Override
	public void end(TraceStatus status) {
		complete(status, null);
	}

	@Override
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

		releaseTraceId();
	}

	//level - 1 씩 감소
	private void releaseTraceId() {
		if (traceIdHolder.isFirstLevel()) {
			traceIdHolder = null; //destroy
		} else {
			traceIdHolder = traceIdHolder.createPreviousId();
		}
	}

	private static String addSpace(String prefix, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append((i == level - 1) ? "|" + prefix : "| ");
		}
		return sb.toString();

	}
}
