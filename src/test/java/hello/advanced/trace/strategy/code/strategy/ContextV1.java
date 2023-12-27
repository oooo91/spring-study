package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 필드에 전략을 보관하는 방식
 */
@Slf4j
public class ContextV1 {

	private Strategy strategy;

	public ContextV1(Strategy strategy) {
		this.strategy = strategy;
	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행

		/**
		 * 위임한다.
		 * 아까 템플릿 메서드 (상속) 은 자식 클래스들이 부모 클래스 (context) 를 상속해서 변하는 부분을 구현했다.
		 * 반면 전략 패턴 (위임) 은 Context 가 Strategy 에 위임(일을 맡기다)한다. 이 Strategy 가 알아서 변하는 부분을 구현하도록 해
		 * 그럼 또 뭐가 좋냐
		 * 상속일 경우에는 자식 클래스가 본인이 쓰지도 않는 부모 클래스의 코드들을 다 알고 있음 (의존성 높다)
		 * 위임일 경우에는 Context 가 Strategy 만 알고 있으면 되기 떄문에, 즉 구현체는 몰라도 되기 때문에 의존성이 낮다.
		 * 스프링도 이 전략 패턴을 사용한다.
		 *
		 * 상속 = 구현
		 * 위임 = 주입
		 *
		 * 상속 = Context 가 바뀌면 영향을 받는데, (상속이니까)
		 * 위임 = Context 가 바뀌든 말든 Strategy 는 영향 안 받는다.
		 */
		strategy.call();
		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

}
