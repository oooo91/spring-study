package hello.core.common;

import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

//로그를 출력하기 위한 클래스
//http 요청 당 하나씩 생성되고 요청이 끝난 시점에 소멸된다.
//빈이 생성되는 시점에 @PostConstruct를 사용하여 uuid를 생성하여 저장한다. uuid를 저장하는 이유는 서로 다른 http를 구분하기 위해서다.
//requestUrl은 빈이 생성되는 시점에는 알 수 없으므로 외부에서 setter로 받는다.
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS) //MyLogger 프록시를 만듦
public class MyLogger {

	private String uuid;
	private String requestURL;

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public void log(String message) {
		System.out.println("[" + uuid + "]" + "[" + requestURL + "]" + message);
	}

	@PostConstruct
	public void init() {
		uuid = UUID.randomUUID().toString(); //unique id (절대로 겹치지 않음)
		System.out.println("[" + uuid + "] request scope bean create : " + this);
	}

	@PreDestroy
	public void close() {
		System.out.println("[" + uuid + "] request scope bean close : " + this);
	}
}
//웹 환경에만 동작한다.
//요청하는 시점에 생성된다.
//프로토타입과 다르게 종료될 때까지 스프링이 관리하기 때문에 predestroy가 호출된다.
//request : http 하나당 각각 스코프가 생성된다. 각 요청마다 별도의 빈 인스턴스가 생성되고 관리된다.
//즉 request마다 전용으로 객체가 만들어진다 = http request가 같으면 같은 인스턴스를 바라보게 된다.
//프로토타입은 요청마다 빈이 생성된다면 웹 스코프는 응답이 나가기 전까지의 라이프 사이클 동안은 같은 애가 관리된다.

