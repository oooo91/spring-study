package hello.advanced.app.v0;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV0 {

	//저장 로직, ex 인 경우는 예외 발생
	public void save(String itemId) {
		if (itemId.equals("ex")) {
			throw new IllegalStateException("예외 발생");
		}
		sleep(1000); //저장하는 데 1초 걸린다고 가정
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
