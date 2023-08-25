package hello.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
@RestController
public class RequestHeaderController {
    /**
     * 서블릿 할 때 HttpServletRequest에 헤더를 조회하는 편리한 메서드를 배웠다.
     * 어노테이션 기반의 스프링 컨트롤러는 더 다양하고 편하게 조회할 수 있는 메서드 제공한다.
     */

    /**
     * MultiValueMap
     * MAP과 유사한데, 하나의 키에 여러 값을 받을 수 있다.
     * HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다.
     * keyA=value1&keyA=value2

     MultiValueMap -> key 중복 가능한 Map
     * MultiValueMap<String, String> map = new LinkedMultiValueMap();
     * map.add("keyA", "value1");
     * map.add("keyA", "value2");
     * List<String> values = map.get("keyA")
     */
    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpMethod httpMethod,
                          Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap, //모든 header 조회
                          @RequestHeader("host") String host, //단일 조회
                          @CookieValue(value = "myCookie", required = false) String cookie) { //value = '키 값'

        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);

        return "ok";

    }
}
