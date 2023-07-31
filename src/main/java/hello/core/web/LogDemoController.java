package hello.core.web;

import hello.core.common.MyLogger;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LogDemoController {

	private final LogDemoService logDemoService;
	//private final MyLogger myLogger; //생성자 시 주입되어 오류난다.
	//private final ObjectProvider<MyLogger> myLoggerProvider; //myLogger를 주입받는게 아니라, myLogger를 찾는 DL을 주입한 것이다.
	private final MyLogger myLogger; //프록시 사용할 것이다. Provider 안 쓰고 프록시 쓰면 더 편리하다. 기존 코드 그대로 쓰더라도 오류가 나지 않는다.

	@RequestMapping("log-demo")
	@ResponseBody
	public String logDemo(HttpServletRequest request) {
		//MyLogger myLogger = myLoggerProvider.getObject(); //이때 스코프가 생성됨

		String requestURL = request.getRequestURI().toString();
		myLogger.setRequestURL(requestURL);

		myLogger.log("controller test");
		logDemoService.log("testId"); //서비스에서도 호출해보자
		return "OK";
	}
}

//스프링 컨테이너가 떠서 -> LogDemoService, myLogger 얘네 주입 받아야하니까 myLogger 내놓으라고 하는데 ->
//문제는 myLogger는 request scope이고, 이때 request가 없어서 에러가 발생한다. (요청 안한 상태니까.)
//이때 Provider를 쓰면 해결된다
//(이 빈은 실제 고객의 요청이 와야 생성되어야하므로, 컨테이너에서 이 빈을 주세요 를 주입 단계가 아니라 요청 단계에서 받기 위해 '지연'시켜야한다는 것이다.)

//근데 프록시를 쓰면 기존 코드를 사용하더라도 오류가 나지 않는다.
//동작 : 가짜 프록시 객체를 만들어서 주입시킨다 -> 실제 빈의 기능을 호출하는 시점에 이때 진짜를 찾아서 동작한다.
//즉 CGLIB라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어 주입시키기 때문에 오류가 나지 않는 것이다.