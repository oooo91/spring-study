package hello.advanced.trace;

import java.util.UUID;

public class TraceId {

	private String id;
	private int level;

	public TraceId() {
		this.id = creatId();
		this.level = 0;
	}

	private TraceId(String id, int level) {
		this.id = id;
		this.level = level;
	}

	//http 요청을 구분하기 위한 uuid
	private String creatId() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	//level 에 따라 depth 가 달라질 것이다.
	public TraceId createNextId() {
		return new TraceId(id, level + 1);
	}

	public TraceId createPreviousId() {
		return new TraceId(id, level - 1);
	}

	public boolean isFirstLevel() {
		return level == 0;
	}

	public String getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}
}
