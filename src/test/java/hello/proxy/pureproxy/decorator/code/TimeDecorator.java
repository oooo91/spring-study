package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeDecorator implements Component {

	/**
	 * 프록시 (데코레이터) 들은 항상 부가 기능을 필요로 하는 대상을 주입해야한다.
	 * 결국은 TimeDecorator 과 MessageDecorator 에서
	 * private Component component;

	 * 	public TimeDecorator(Component component) {
	 * 		this.component = component;
	 *        }
	 * 가 중복이 일어난다. 이걸 추상 클래스를 만들어서 공통 부분만 뽑아볼까? (Decorator)
	 */

	private Component component;

	public TimeDecorator(Component component) {
		this.component = component;
	}

	@Override
	public String operation() {

		log.info("TimeDecorator 실행");
		long startTime = System.currentTimeMillis();

		//프록시1 -> 프록시2 를 호출 (프록시 체인)
		String result = component.operation();
		long endTime = System.currentTimeMillis();

		long resultTime = endTime - startTime;
		log.info("TimeDecorator 종료 resultTime={}ms", resultTime);

		return result;
	}
}
