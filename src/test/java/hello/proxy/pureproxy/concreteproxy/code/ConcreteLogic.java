package hello.proxy.pureproxy.concreteproxy.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 이 구체 클래스만 있는 데에 프록시를 넣어야한다. (얘는 인터페이스가 없다)
 */
@Slf4j
public class ConcreteLogic {

	public String operation() {
		log.info("ConcreteLogic 실행");
		return "data";
	}
}
