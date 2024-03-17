package hello.springmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringmvcApplication {

    /**
     * War가 아니라 Jar를 선택하는 이유 -> JSP를 사용하지 않기 때문에 Jar를 사용하는 것이 좋다.
     * 앞으로 스프링 부트를 사용하면 이 방식을 주로 사용하게 된다.
     * JSP -> webapp 경로를 사용한다. (webapp - WEB-INF ~ )
     * War -> War를 사용하면 내장 서버도 사용가능 하지만, 주로 외부 서버에 배포하는 목적으로 사용된다.
     * Jar -> 항상 내장 서버(톰캣등)를 사용하고, webapp 경로도 사용하지 않는다. 내장 서버 사용에 최적화 되어 있는 기능
     * 스프링 부트에 Jar 를 사용하면 /resources/static/ 위치에 index.html 파일을 두면 Welcome 페이지로 처리해준다.
     * (스프링 부트가 지원하는 정적 컨텐츠 위치에 /index.html 이 있으면 된다. (스프링이 이 위치를 지원한다.)
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringmvcApplication.class, args);
    }

}
