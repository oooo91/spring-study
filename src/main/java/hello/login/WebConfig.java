package hello.login;

import hello.login.web.argumentResolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LoginCheckInterceptor;
import hello.login.web.interceptor.LoginInterceptor;
import java.util.List;
import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  //ArgumentResolver 등록
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new LoginMemberArgumentResolver());
  }

  //인터셉터 등록, implements WebMvcConfigurer, 서블릿과 달리 필터 형식, 서블릿과 달리 모든 경로 /** 별 두개
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginInterceptor())
        .order(1)
        .addPathPatterns("/**")
        .excludePathPatterns("/css/**", "/*.ico", "/error");

    registry.addInterceptor(new LoginCheckInterceptor())
        .order(2)
        .addPathPatterns("/**")
        .excludePathPatterns("/", "/members/add", "/login", "/logout", "/css/**", "/*.ico", "/error");
  }

  //필터 등록, 스프링 부트가 WAS 내장해서 띄우기 때문에 WAS 를 띄울 때 필터를 주입한다.
  //@Bean
  public FilterRegistrationBean logFilter() {

    FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
    filterFilterRegistrationBean.setFilter(new LogFilter());
    filterFilterRegistrationBean.setOrder(1); //필터 순서
    filterFilterRegistrationBean.addUrlPatterns("/*"); //적용할 url

    return filterFilterRegistrationBean;
  }

  //로그인 체크 필터
  //@Bean
  public FilterRegistrationBean logCheckFilter() {

    FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
    filterFilterRegistrationBean.setFilter(new LoginCheckFilter());
    filterFilterRegistrationBean.setOrder(2); //필터 순서

    //적용할 url -> 여기서도 여러 url 을 적용할 수 있는데 개발 중 새로 생기는 url 이 있ㅇ면 계속 추가해야 하니까 보통은 /* 로 잡고,
    //대신에 filter 에서 whiteList 처럼 배열로 잡는 방법을 사용한다.
    filterFilterRegistrationBean.addUrlPatterns("/*");
    return filterFilterRegistrationBean;
  }

}
