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
