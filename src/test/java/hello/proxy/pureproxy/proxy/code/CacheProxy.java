package hello.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheProxy implements Subject {

	private Subject target; //실제 호출되는 객체
	private String cacheValue; //캐시 저장소

	//클라이언트는 -> 프록시를 참조하고 -> 프록시는 realSubject 를 참조해야하므로 subject 를 주입한다.
	public CacheProxy(Subject target) {
		this.target = target;
	}

	@Override
	public String operation() {
		log.info("프록시 호출");
		if (cacheValue == null) {
			cacheValue = target.operation(); //캐시 데이터가 없으면 실제 로직을 호출한다.
		}
		return cacheValue;
	}
}
