package hello.advanced.trace.threadlocal;

import static java.lang.Thread.sleep;

import hello.advanced.trace.threadlocal.code.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FieldServiceTest {

	private FieldService fieldService = new FieldService();

	@Test
	void field() {
		log.info("main start");

		/**
		 * Runnable 인터페이스:
		 *
		 * Runnable 인터페이스는 스레드가 실행할 수 있는 작업을 정의한다.
		 * Runnable은 단일 메서드를 가지고 있으며, 이 메서드는 스레드가 실행될 때 호출되는 run() 메서드다.
		 * 스레드가 실행할 작업을 Runnable을 구현한 클래스의 인스턴스로 정의할 수 있다.
		 * public class MyRunnable implements Runnable {
		 *     @Override
		 *     public void run() {
		 *         // 스레드가 실행할 작업 정의
		 *     }
		 *
		 * Thread 클래스는 자바에서 스레드를 직접 생성하고 제어하기 위한 클래스다.
		 * Thread 클래스의 생성자는 Runnable 객체를 받을 수 있습니다. 따라서 Runnable을 구현한 클래스의 인스턴스를 생성자에 전달하여 스레드를 생성할 수 있다.
		 * Runnable myRunnable = new MyRunnable();
		 * Thread myThread = new Thread(myRunnable);
		 * myThread.start(); // 스레드 시작
		 */

		/* Runnable userA = new Runnable() {
			@Override
			public void run() {
				fieldService.logic("userA");
			}
		}; */

		Runnable userA = () -> {
			fieldService.logic("userA"); //1초 쉬자
		};
		Runnable userB = () -> {
			fieldService.logic("userA");
		};

		Thread threadA = new Thread(userA);
		threadA.setName("thread-A");
		Thread threadB = new Thread(userB);
		threadA.setName("thread-B");

		threadA.start();
		sleep(2000); //main 2초 쉬기 -> 동시성 문제 안 생기도록 한다. 100 이면 동시성 발생, nameStore 공유 자원(또는 싱글톤)이라서;
		threadB.start();
		sleep(2000); //main 2초 쉬기 -> thread b 도 다 수행할 수 있도록 한다.

		/**
		 * 지금처럼 싱글톤 객체의 필드를 사용하면서 동시에 문제를 해결하려면 어떻게 해야할까?
		 * 쓰레드 로컬을 사용하자 (해당 스레드만 접근할 수 있는 특별한 저장소)
		 */
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
