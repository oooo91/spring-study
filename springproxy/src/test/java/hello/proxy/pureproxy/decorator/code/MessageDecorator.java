package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

//얘가 프록시
@Slf4j
public class MessageDecorator implements Component {

	private Component component; //실제 객체

	public MessageDecorator(Component component) {
		this.component = component;
	}

	//별표 붙이는 부가기능
	@Override
	public String operation() {
		log.info("MessageDecorator 실행");
		String result = component.operation();
		String decoResult = "*****" + result + "*****";
		log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result,
			decoResult);
		return decoResult;
	}
}
