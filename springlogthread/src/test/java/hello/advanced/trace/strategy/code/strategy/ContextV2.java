package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextV2 {

	/**
	 * 전략을 필드가 아니라 파라미터로 받게 한다. (선 조립 후 실행)
	 * 의존 관계를 미리 주입하는 게 아니라 전달하는 방식 (실행할 때 원하는 전략 유연하게 넣기)

	 * 여기서 Strategy 는 콜백이다.
	 * 콜백은 인수로 넘겨주는 실행 가능한 코드를 말한다. (그래서 자바스크립트에서 인수로 function 을 넘길 때 콜백이라고 하는 구나)
	 * 왜 콜백이라고 이름 붙였냐? -> Strategy 라는 실행 가능한 코드가 뒤에서 즉, Context 에서 실행되기 떄문에 콜(실행된다) 백(뒤에서) 이라고 이름 붙였다.
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
