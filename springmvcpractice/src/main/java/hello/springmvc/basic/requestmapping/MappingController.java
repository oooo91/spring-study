package hello.springmvc.basic.requestmapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MappingController {

    @GetMapping({"/hello-basic", "hello-go"}) // {} -> or 조건
    public String helloBasic() {
        log.info("HelloBasic");
        return "ok";
    }

    /**
     * 최근 HTTP API는 다음과 같이 리소스 경로(url)에 식별자(값)를 넣는 스타일(경로변수)을 선호한다.
     * /mapping/userA
     * /users/1
     * @RequestMapping 은 URL 경로를 템플릿화 할 수 있는데, @PathVariable 을 사용하면 매칭 되는 부분을 편리하게 조회할 수 있다.
     * @PathVariable 의 이름과 파라미터 이름이 같으면 생략할 수 있다
     * @PathVariable("userId") String userId -> @PathVariable String userId
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data) {
        log.info("mappingPath userId={}", data);
        return "ok";
    }

    /**
     * 파라미터로 추가 매핑
     * params="mode",
     * params="!mode"
     * params="mode=debug"
     * params="mode!=debug" (! = )
     * params = {"mode=debug","data=good"}

     * params = "mode=debug" 이게 뭔말이냐면 -> mode=debug가 있어야지 호출이 된다는 소리다.
     * 그래서 /mapping-param?mode=debug 일 경우 잘 호출이 되는데 그냥 /mapping-param이거나 해당 파라미터가 없으면 404가 뜬다.
     * 근데 잘 사용 안한대. 경로 변수 스타일을 더 많이 사용한다고 한다.
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }

    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",
     * headers="!mode"
     * headers="mode=debug"
     * headers="mode!=debug" (! = )

     * 마찬가지로 header에 mode 라는 키에 debug 값이 들어가야 호출이 된다.
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }

    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * MediaType.APPLICATION_JSON_VALUE

     * consumes = {Content-type} 을 쓰면 해당 타입일 경우에만 호출이 된다.
     * HTTP 요청의 Content-Type 헤더를 기반으로 미디어 타입으로 매핑한다.
     * 만약 맞지 않으면 HTTP 415 상태코드(Unsupported Media Type)을 반환한다.

     * 예시
     * consumes = "text/plain"
     * consumes = {"text/plain", "application/*"}
     * consumes = MediaType.TEXT_PLAIN_VALUE
     */
    @PostMapping(value = "/mapping-consume", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }

    /**
     * Accept 헤더 기반 Media Type
     * produces = "text/html"
     * produces = "!text/html"
     * produces = "text/*"
     * produces = "*\/*"

     * HTTP 요청의 Accept 헤더를 기반으로 미디어 타입으로 매핑한다.
     * 만약 맞지 않으면 HTTP 406 상태코드(Not Acceptable)을 반환한다.

     * 예시
     * produces = "text/plain"
     * produces = {"text/plain", "application/*"}
     * produces = MediaType.TEXT_PLAIN_VALUE
     * produces = "text/plain;charset=UTF-8
     */
    @PostMapping(value = "/mapping-produce", produces = MediaType.TEXT_HTML_VALUE)
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }


}
