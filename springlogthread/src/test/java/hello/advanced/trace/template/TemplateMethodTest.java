package hello.advanced.trace.template;

import hello.advanced.trace.template.code.AbstractTemplate;
import hello.advanced.trace.template.code.SubClassLogic1;
import hello.advanced.trace.template.code.SubClassLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateMethodTest {

	//템플릿 메서드가 왜 필요한지 알아보자 -> 우선 핵심 로직과 부가 기능(로그)이 공존하게 작성하자
	@Test
	void templateMethodV0() {
		logic1();
		logic2();
	}

	private void logic1() {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행
		log.info("비즈니스 로직1 실행");
		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

	private void logic2() {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행
		log.info("비즈니스 로직2 실행");
		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

	/**
	 * 템플릿 메서드 패턴을 적용한 예제다 -> 오버라이딩된 게 호출됨
	 */
	@Test
	void templateMethodV1() {
		AbstractTemplate template1 = new SubClassLogic1(); //이게 중요
		template1.execute();

		AbstractTemplate template2 = new SubClassLogic2(); //이게 중요
		template2.execute();
	}

	/**
	 * 대신 위 방법은 계속 class 를 생성해야한다 -> 익명 클래스로 만들자
	 */
	@Test
	void templateMethodV2() {
		AbstractTemplate template1 = new AbstractTemplate() {
			@Override
			protected void call() {
				log.info("비즈니스 로직1 실행");
			}
		};
		template1.execute();

		AbstractTemplate template2 = new AbstractTemplate() {
			@Override
			protected void call() {
				log.info("비즈니스 로직2 실행");
			}
		};
		template2.execute();
	}
}
