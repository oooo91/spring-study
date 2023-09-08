package hello;

import hello.boot.MySpringApplication;
import hello.boot.MySpringBootApplication;

/**
 * @MySpringBootApplication에 componentScan 기능이 안에 있다 -> appContext.register(configClass); 에 적용된다.
 * MySpringApplication 에서 configClass 파라미터에 MySprigBootMain.class가 들어가는데
 * 이 클래스의 패키지(import hello) 하위의 클래스는 다 scan하겠다는 뜻이다.
 * 부트 클래스 실행 -> 내장 톰캣 실행, 스프링 컨테이너 생성, 디스패처 생성 -> 스프링 톰캣 연결, 컴포넌트 스캔 다 된다.
 * 그러니까 EmbedTomcatSpringMain 처럼 낱개로 Config.class 컨테이너에 등록하지 않아도 된다.
 */
@MySpringBootApplication
public class MySpringBootMain {
    public static void main(String[] args) {
        System.out.println("MySpringBootMain.main");
        MySpringApplication.run(MySpringBootMain.class, args);
    }
}
