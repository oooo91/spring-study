package hello.proxy.jdkdynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ReflectionTest {

	@Slf4j
	static class Hello {
		public String callA() {
			log.info("callA");
			return "A";
		}

		public String callB() {
			log.info("callB");
			return "B";
		}
	}

	@Test
	void reflection0() {
		Hello target = new Hello();

		//공통 로직1 시작
		log.info("start");
		String result1 = target.callA(); //호출하는 메서드만 다름, target 에 종속된 메서드라 분리하기가 어려움
		log.info("result={}", result1);
		//공통 로직1 종료

		//공통 로직2 시작
		log.info("start");
		String result2 = target.callB(); //호출하는 메서드만 다름
		log.info("result={}", result2);
		//공통 로직2 종료

	}

	//리플랙션 사용하여 추상화 하자 (추상화; 프로그램의 구조를 단순화하고 더 일반화된 형태로 만들어서 코드의 유연성을 향상시키는 과정)
	@Test
	void reflection1() throws Exception {
		Class<?> classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello"); //Hello 클래스 메타정보 얻기

		Hello target = new Hello();

		//callA 메서드 정보 (Method => 메서드 메타 정보)
		Method methodCallA = classHello.getMethod("callA"); //처음에는 callA() 소스 코드가 있어 분리하기 어려웠는데, 리플랙션을 활용하면 인자로 "동적으로 변경할 수 있다"
		Object result1 = methodCallA.invoke(target);//invoke(부르다) => 동적으로 target 의 callA 를 부른다.
		log.info("result1={}", result1);

		//callB 메서드 정보
		Method methodCallB = classHello.getMethod("callB");
		Object result2 = methodCallB.invoke(target);//invoke(부르다) => 동적으로 target 의 callB 를 부른다.
		log.info("result2={}", result2);
	}

	//리플랙션 적용
	private void dynamicCall(Method method, Object target) throws Exception {
		//공통 로직 시작
		log.info("start");
		Object result = method.invoke(target);
		log.info("result={}", result);
		//공통 로직 종료
	}

	@Test
	void reflection2() throws Exception {
		Class<?> classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello"); //Hello 클래스 메타정보 얻기

		Hello target = new Hello();

		//callA 메서드 정보 (Method => 메서드 메타 정보)
		Method methodCallA = classHello.getMethod("callA"); //처음에는 callA() 소스 코드가 있어 분리하기 어려웠는데, 리플랙션을 활용하면 인자로 "동적으로 변경할 수 있다"
		dynamicCall(methodCallA, target);

		//callB 메서드 정보
		Method methodCallB = classHello.getMethod("callB");
		dynamicCall(methodCallB, target);
	}




}
