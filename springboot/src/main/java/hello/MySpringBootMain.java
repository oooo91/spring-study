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

/**
 * @SpringBootApplication 어노테이션
 * 스프링 부트를 실행할 때는 자바 main() 메서드에서 SpringApplication.run() 을 호출해주면 된다.
 * 여기에 메인 설정 정보를 넘겨주는데, 보통 @SpringBootApplication 애노테이션이 있는 현재 클래스를 지정해주면 된다.
 * 참고로 현재 클래스에는 @SpringBootApplication 애노테이션이 있는데, 이 애노테이션 안에는 컴포넌트 스캔을 포함한 여러 기능이 설정되어 있다.
 * 기본 설정은 현재 패키지와 그 하위 패키지 모두를 컴포넌트 스캔한다

 * 이 단순해 보이는 코드 한줄 안에서는 수 많은 일들이 발생하지만 핵심은 2가지다.
 * 1. 스프링 컨테이너를 생성한다.
 * 2. WAS(내장 톰캣)를 생성한다.
 * 스프링 부트 내부에서 스프링 컨테이너를 생성하는 코드
 * org.springframework.boot.web.servlet.context.ServletWebServerApplicationContextFactory
 * 스프링 부트 내부에서 내장 톰캣을 생성하는 코드
 * org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
 * 그리고 어디선가 내장 톰캣에 디스패처 서블릿을 등록하고, 스프링 컨테이너와 연결해서 동작할 수 있게 한다.

 * 지금까지 스프링 부트가 어떻게 톰캣 서버를 내장해서 실행하는지 스프링 부트의 비밀 하나를 풀어보았다.
 * 다음에는 스프링 부트의 빌드와 배포 그리고 스프링 부트가 제공하는 jar 의 비밀을 알아보자.

 * 스프링 부트 실행 가능 Jar
 * Fat Jar는 하나의 Jar 파일에 라이브러리의 클래스와 리소스를 모두 포함했다.
 * 그래서 실행에 필요한 모든 내용을 하나의 JAR로 만들어서 배포하는 것이 가능했다. 하지만 Fat Jar는 다음과 같은 문제를 가지고 있다.

 * 실행 가능 Jar -> 실행 시 main 메서드가 실행되는 것이 아니라 JarLancher의 main 메서드가 실행된다.
 * 스프링 부트는 이런 문제를 해결하기 위해 jar 내부에 jar를 포함할 수 있는 특별한 구조의 jar를 만들고
 * 동시에 만든 jar를 내부 jar를 포함해서 실행할 수 있게 했다. --> JarLauncher
 * 이것을 실행 가능 Jar(Executable Jar)라고 한다.

 * 이 실행 가능 Jar를 사용하면 다음 문제들을 깔끔하게 해결할 수 있다.
 * 문제: 어떤 라이브러리가 포함되어 있는지 확인하기 어렵다.
 * 해결: jar 내부에 jar를 포함하기 때문에 어떤 라이브러리가 포함되어 있는지 쉽게 확인할 수 있다.
 * 문제: 파일명 중복을 해결할 수 없다.
 * 해결: jar 내부에 jar를 포함하기 때문에 a.jar , b.jar 내부에 같은 경로의 파일이 있어도 둘다 인식할 수 있다.
 * 참고로 실행 가능 Jar는 자바 표준은 아니고, 스프링 부트에서 새롭게 정의한 것이다.

 * JarLauncher 를 통해서 여기에 있는 classes 와 lib 에 있는 jar 파일들을 읽어들인다.
 * 실행 과정 정리
 //-- 자바 표준 기술 시작
 * 1. java -jar xxx.jar
 * 2. MANIFEST.MF 인식
 //-- 자바 표준 기술 끝
 * 3. JarLauncher.main() 실행
 * BOOT-INF/classes/ 인식
 * BOOT-INF/lib/ 인식
 * // -- JarLauncher 을 통해 라이브러리 인식
 * 4. BootApplication.main() 실행

 * > 실행 가능 Jar가 아니라, IDE에서 직접 실행할 때는 BootApplication.main() 을 바로 실행한다.
 * > IDE가 필요한 라이브러리를 모두 인식할 수 있게 도와주기 때문에 JarLauncher 가 필요하지 않다.
 * > 배포할 시에는 gradle로 빌드해야하니까 JarLauncher가 필요한데 IDE에서 고냥 구동할 때는
 * > Jarlancher 필요하지 않기 때문에 그래서 여태 개발 시에 빠르게 빌드하고 싶을 경우 gradle이 아닌 Intellij 로 설정을 했던 것이와요.
 */
@MySpringBootApplication
public class MySpringBootMain {
    public static void main(String[] args) {
        System.out.println("MySpringBootMain.main");
        MySpringApplication.run(MySpringBootMain.class, args);
    }
}
