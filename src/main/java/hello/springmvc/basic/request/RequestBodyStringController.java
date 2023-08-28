package hello.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class RequestBodyStringController {

    /**
     * 요청 파라미터(쿼리 스트링)와 다르게(get, post form), HTTP 메시지 바디를 통해 데이터가 >직접< 넘어오는 경우는
     * @RequestParam , @ModelAttribute 를 사용할 수 없다. (물론 HTML Form 형식으로 전달되는 경우는 요청 파라미터로 인정된다.)
     * 먼저 가장 단순한 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고, 읽어보자.
     * HTTP 메시지 바디의 데이터를 InputStream 을 사용해서 직접 읽을 수 있다.
     */
    @PostMapping("/request-body-string-v1")
    public void requestBodyString(HttpServletRequest request, HttpServletResponse response)
                                                                        throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); //바이트 -> String
        log.info("messageBody={}", messageBody);
        response.getWriter().write("ok");
    }

    /**
     * InputStream과 Writer을 직접 받을 수 있다.
     * InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
     * OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
     */
    @PostMapping("/request-body-string-v2")
    public void requestBodyStringV2(InputStream inputStream, Writer responseWriter)
                                                                throws IOException {
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("messageBody={}", messageBody);
        responseWriter.write("ok");
    }

    /**
     * Stream도 직접 쓰고 싶지 않으면 -> HttpEntity
     * HttpEntity: HTTP header, body 정보를 편리하게 조회
     * - 메시지 바디 정보를 직접 조회 (@RequestParam X, @ModelAttribute X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용

     * 응답에서도 HttpEntity 사용 가능
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용

     * Converter 란?
     * StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); 이런 것들 자동으로 처리해주는 애

     * 스프링 MVC는 다음 파라미터를 지원한다.
     * HttpEntity: HTTP header, body 정보를 편리하게 조회
     * 메시지 바디 정보를 직접 조회
     * 요청 파라미터(쿼리스트링: get, post form)를 조회하는 기능과 관계 없음 @RequestParam X, @ModelAttribute X

     * HttpEntity는 응답에도 사용 가능
     * 메시지 바디 정보 직접 반환
     * 헤더 정보 포함 가능
     * view 조회X

     * HttpEntity 를 상속받은 다음 객체들도 같은 기능을 제공한다.
     * RequestEntity -> HttpMethod, url 정보가 추가, 요청에서 사용
     * ResponseEntity -> HTTP 상태 코드 설정 가능, 응답에서 사용
     * ex) return new ResponseEntity<String>("Hello World", responseHeaders, HttpStatus.CREATED)
     */
    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) {
        String messageBody = httpEntity.getBody(); //http 메시지 body 직접 꺼내기
        log.info("messageBody={}", messageBody);
        //return new HttpEntity<>("ok");
        return new ResponseEntity<>("ok", HttpStatus.CREATED);
    }

    /**
     * 근데 HttpEntity도 쓰고 싶지 않다!
     * @RequestBody 어노테이션만 붙이면 된다.
     * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용

     * @ResponseBody
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     */
    @ResponseBody
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringV4(@RequestBody String messageBody) {
        log.info("messageBody={}", messageBody);
        return "ok";
    }
}
