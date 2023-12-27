package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextV2 {

	/**
	 * 전략을 필드가 아니라 파라미터로 받게 한다. (선 조립 후 실행)
	 * 의존 관계를 미리 주입하는 게 아니라 전달하는 방식 (실행할 때 원하는 전략 유연하게 넣기)
	 */
	public void execute(Strategy strategy) {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행

		strategy.call(); //위임

		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

}
