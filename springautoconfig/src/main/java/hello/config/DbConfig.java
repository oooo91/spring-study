package hello.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

/**
 * 1. 수동 등록
 * @Configuration 사용

 * 2-1. 자동 등록
 * @Configuration 주석처리
 * componentScan이 아예 안돼서 @Bean 읽지 않으나 스프링부트가 일반적으로 자주 사용하는 수 많은 빈들을 자동으로 등록해준다.
 * 스프링부트는 web, h2, jdbc등등 약 100여가지와 관련하여 자동 설정(starter)을 제공한다.

 * 2-2. 자동 등록
 * @AutoConfiguration 사용
 * @AutoConfiguration 가 왜 필요한가 -> 외부 라이브러리와 관련이 크다.
 * 외부 라이브러리는 그냥 클래스 집합이다.
 * 따라서 라이브러리를 임포트하더라도 자동으로 라이브러리에 있는 빈이 등록되지 않기 때문에
 * 사용자들은 라이브러리 내부에 있는 어떤 빈을 등록해야하는지 알아야 하고, 그것을 또 하나하나 빈으로 등록해야 한다.
 * 즉 @Configuration 클래스 아래에 @Bean을 일일이 주입해야한다.

 * 이런 부분을 자동으로 처리해주는 것이 바로 스프링 부트 자동 구성(Auto Configuration)이다. 이러면 빈을 등록하는 별도의 설정을 하지 않아도 된다.
 * @AutoConfiguration 사용하므로써 새로 추가되는 라이브러리(jar)는 스프링부트 자동설정 의존성에 따라 설정이 자동 적용된다.
 * 또한 라이브러리 특성상 어떤 상황에서는 적용이 되고, 어떤 상황에서는 적용되지 않게 하려면
 * @AutoConfiguration
 * @ConditionalOnProperty(name = "memory", havingValue = "on") 라이브러리를 이런식으로 구성하면 된다. 유연성이 높다.
 * 그뿐 아니라 @ConditionalOnXxx 덕분에 라이브러리 설정을 유연하게 제공할 수 있다.
 * 스프링 부트는 수 많은 자동 구성을 제공한다. 그 덕분에 스프링 라이브러리를 포함해서 수 많은 라이브러리를 편리하게 사용할 수 있다.

 * 2-3. 자동 등록하기
 * 스프링 부트는 시작 시점에 /resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 * 의 정보를 읽어서 자동구성으로 사용한다.
 * 따라서 내부에 있는 MemoryAutoConfig 가 자동으로 실행된다. (MemoryAutoConfig 구성 @AutoConfiguration 아래에 @Bean)

 * 2-4. 스프링부트는 자동 등록을 어떻게 읽나
 * 스프링 부트 자동 구성이 동작하는 원리는 다음 순서로 확인할 수 있다.
 * @SpringBootApplication > @EnableAutoConfiguration > @Import(AutoConfigurationImportSelector.class)
 * 스프링 부트는 보통 다음과 같은 방법으로 실행한다.
 * @SpringBootApplication의 run()을 보면 AutoConfigApplication.class 를 넘겨주는데, 이 클래스를 설정 정보로 사용한다는 뜻이다.
 * @AutoConfigApplication 에는 @SpringBootApplication 애노테이션이 있는데, 여기에 중요한 설정 정보들이 들어있다.
 * 여기서 우리가 주목할 애노테이션은 @EnableAutoConfiguration 이다. 이름 그대로 자동 구성을 활성화하는 기능을 제공한다.
 * @EnableAutoConfiguration
 * @AutoConfigurationPackage
 * @Import(AutoConfigurationImportSelector.class)
 * public @interface EnableAutoConfiguration {…}
 * @Import 는 주로 스프링 설정 정보( @Configuration )를 포함할 때 사용한다.
 * 그런데 AutoConfigurationImportSelector 를 열어보면 @Configuration 이 아니다.
 * 이 기능을 이해하려면 ImportSelector 에 대해 알아야 한다. --> /test/selector/ImportSelectorTest 에 예제코드 있다.

 * @EnableAutoConfiguration 동작 방식
 * 이제 ImportSelector 를 이해했으니 다음 코드를 이해할 수 있다.
 * @EnableAutoConfiguration
 * @AutoConfigurationPackage
 * @Import(AutoConfigurationImportSelector.class)
 * public @interface EnableAutoConfiguration {…}
 * AutoConfigurationImportSelector 는 ImportSelector 의 구현체이다. 따라서 설정 정보를 동적으로 선택할 수 있다.
 * 실제로 이 코드는 모든 라이브러리에 있는 다음 경로의 파일을 확인한다.
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 * 그리고 파일의 내용을 읽어서 설정 정보로 선택한다.

 * 스프링 부트 자동 구성이 동작하는 방식은 다음 순서로 확인할 수 있다.
 * @SpringBootApplication > @EnableAutoConfiguration >@Import(AutoConfigurationImportSelector.class)
 * > resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 파일을 열어서 설정 정보 선택
 * 해당 파일의 설정 정보가 스프링 컨테이너에 등록되고 사용

 * 정리
 * 스프링 부트의 자동 구성을 직접 만들어서 사용할 때는 다음을 참고하자.
 * @AutoConfiguration 에 자동 구성의 순서를 지정할 수 있다.
 * @AutoConfiguration 도 설정 파일이다. 내부에 @Configuration 이 있는 것을 확인할 수 있다.
 * 하지만 일반 스프링 설정과 라이프사이클이 다르기 때문에 컴포넌트 스캔의 대상이 되면 안된다.
 * 파일에 지정해서 사용해야 한다.
 * resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 * 그래서 스프링 부트가 제공하는 컴포넌트 스캔에서는 @AutoConfiguration 을 제외하는 AutoConfigurationExcludeFilter 필터가 포함되어 있다.
 * 자동 구성이 내부에서 컴포넌트 스캔을 사용하면 안된다. 대신에 자동 구성 내부에서 @Import 는 사용할 수 있다.

 * 그런데 이런 방식으로 빈이 자동 등록되면, 빈을 등록할 때 사용하는 설정 정보는 어떻게 변경해야 하는지
 * 의문이 들 것이다. 예를 들어서 DB 접속 URL, ID, PW 같은 것 말이다. 데이터소스 빈을 등록할 때 이런
 * 정보를 입력해야 하는데, 빈이 자동으로 다 등록이 되어 버린다면 이런 정보를 어떻게 입력할 수 있을까?
 * 다음 장을 통해 알아보자.
 */
@Slf4j
@Configuration
public class DbConfig {

    @Bean
    public DataSource dataSource() {
        log.info("dataSource 빈 등록");
        HikariDataSource dataSource = new HikariDataSource(); //hikari connection
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public TransactionManager transactionManager() {
        log.info("transactionManager 빈 등록");
        return new JdbcTransactionManager(dataSource());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        log.info("jdbcTemplate 빈 등록");
        return new JdbcTemplate(dataSource() );
    }
}
