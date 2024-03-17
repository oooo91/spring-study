package hello.advanced.config;

import hello.advanced.trace.logtrace.FieldLogTrace;
import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {

	//fieldLogTrace bean 으로 등록하기
	@Bean
	public LogTrace logTrace() {
		//return new FieldLogTrace();
		return new ThreadLocalLogTrace();
	}
}
