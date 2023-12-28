package hello.proxy.app.v2;

//이번에는 인터페이스가 아니라 구현체만으로
public class OrderRepositoryV2 {

	public void save(String itemId) {
		//저장 로직
		if (itemId.equals("ex")) {
			throw new IllegalStateException("예외 발생!");
		}
		sleep(1000);
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
