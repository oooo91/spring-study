package hello.login.web.filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.info("log filter init");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    log.info("log filter doFilter");
    //ServletRequest 을 다운 캐스팅 해준다. Http 만 받지 않기 때문에
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestURI = httpRequest.getRequestURI();

    //요청 구분
    String uuid = UUID.randomUUID().toString();

    try {
      log.info("REQUEST [{}][{}]", uuid, requestURI);
      chain.doFilter(request, response); //다음 필터 호출, 필터 없으면 서블릿 호출 (스프링인 경우 디스패처 서블릿 -> 컨트롤러 호출)
    } catch (Exception e) {
      throw e;
    } finally {
      log.info("RESPONSE [{}][{}]", uuid, requestURI); //위의 로직 끝나면 RESPONSE 로그 찍힘
    }
  }

  @Override
  public void destroy() {
    log.info("log filter destroy");
  }
}
