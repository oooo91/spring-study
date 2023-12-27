package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTemplate {

	/**
	 * 왜 이게 템플릿 메서드 패턴이냐?
	 * 템플릿을 사용한다 = 템플릿이라는 틀이 있고 거기서 필요한 부분만 조금씩 수정하는 것
	 * 즉, 템플릿 메서드란 메서드로 위처럼 구현하겠다는 것으로 변하는 부분은 메서드로 구현한다.
	 * AbstractTemplate 은 공통 기능을 가져온 템플릿이고, 그 아래에 call() 메서드를 사용해서 변하는 부분을 구현한다.
	 */

	public void execute() {
		long startTime = System.currentTimeMillis();

		//비즈니스 로직 실행
		call(); //상속

		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

	protected abstract void call();
}
