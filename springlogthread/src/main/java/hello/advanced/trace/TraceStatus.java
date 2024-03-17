package hello.advanced.trace;

public class TraceStatus {

	private TraceId traceId;
	private String message;
	private Long startTimeMs; //실행 시간 찾기 위함

	public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
		this.traceId = traceId;
		this.message = message;
		this.startTimeMs = startTimeMs;
	}

	public TraceId getTraceId() {
		return traceId;
	}

	public String getMessage() {
		return message;
	}

	public Long getStartTimeMs() {
		return startTimeMs;
	}
}
