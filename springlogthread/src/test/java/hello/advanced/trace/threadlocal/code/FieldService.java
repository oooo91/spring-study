package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 동시성 이슈가 어떻게 발생하는지 테스트 코드
 */
@Slf4j
public class FieldService {

	private String nameStore;

	public String logic(String name) {
		log.info("저장 name={} -> nameStore={}", name, nameStore);
		nameStore = name; //nameStore 에 저장할 것이다.
		sleep(1000);
		log.info("조회 nameStore={}", nameStore);
		return nameStore;
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
