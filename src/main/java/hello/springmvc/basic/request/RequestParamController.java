package hello.springmvc.basic.request;

import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class RequestParamController {
    /**
     * @RequestParam 사용
     * - 파라미터 이름으로 바인딩
     * @ResponseBody 추가
     * - View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력
     */
    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username={}, age={}", username, age);

        response.getWriter().write("ok"); //void에 response 값 쓰면 -> String "ok" 가 반환된다.
    }

    /**
     * /ok 라는 뷰를 찾는 게 아니라 "ok" 반환 (RestController 역할과 같음)
     */
    @ResponseBody
    @RequestMapping("/request-param-v2")
    public String requestParamV2(@RequestParam("username") String memberName,
                                 @RequestParam("age") int memberAge) {
        log.info("username={}, age={}", memberName, memberAge);
        return "ok";
    }

    /**
     * @RequestParam 사용
     * HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능
     */
    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamV3(
            @RequestParam String username,
            @RequestParam int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능
     * String, int, Integer 등의 단순 타입이면 @RequestParam 도 생략 가능 (내가 만든 DTO 같은 거 말고)
     * 근데 어노테이션 붙이는 게 유지보수 측면에서 좋은 것 같다.
     */
    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamV4(String username, int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /** 주의할 점
     1. username 파라미터가 아예 안오면? -> username이 없으므로 예외
     2. String username 파라미터 이름은 오되 값이 안 들어가면? (?username=) ->  ""(빈 문자)로 받아서 -> 에러가 안 떨어진다.
     3. int age 에 값이 안 들어가면 ? -> 에러 뜬다. int는 null을 받을 수 없으므로 Integer로 받아야 에러가 안 떨어진다.(또는 다음에 나오는 defaultValue 사용)
     4. 기본값이 파라미터 필수( true )이다.
     */
    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * defaultValue
     * 파라미터 안올 때도 기본값 설정 가능하다.
     * 빈 문자의 경우에도 적용
     * /request-param-default?username=
     */
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username,
            @RequestParam(required = false, defaultValue = "-1") int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * @RequestParam Map, MultiValueMap
     * Map(key=value)
     * MultiValueMap(key=[value1, value2, ...]) ex) (key=userIds, value=[id1, id2])

     * 파라미터를 Map, MultiValueMap으로 조회할 수 있다.
     * 파라미터의 값이 1개가 확실하다면 Map 을 사용해도 되지만, 그렇지 않다면 MultiValueMap 을 사용하자
     */
    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
        log.info("username={}, age={}",
                paramMap.get("username"),
                paramMap.get("age"));
        return "ok";
    }

    /**
     * 실제 개발을 하면 요청 파라미터를 받아서 필요한 객체를 만들고 그 객체에 값을 넣어주어야 한다. 보통 다음과 같이 코드를 작성할 것이다.
     * @RequestParam String username;
     * @RequestParam int age;
     * HelloData data = new HelloData();
     * data.setUsername(username);
     * data.setAge(age);
     * 스프링은 이 과정을 완전히 자동화해주는 @ModelAttribute 기능을 제공한다
     * 참고: model.addAttribute(helloData) 코드도 함께 자동 적용됨, 뒤에 model을 설명할 때

     * 스프링 MVC는 @ModelAttribute 가 있으면 다음을 실행한다.
     * 1. HelloData 객체를 생성한다.
     * 2. 요청 파라미터의 이름으로 HelloData 객체의 프로퍼티를 찾는다. 그리고 해당 프로퍼티의 setter를 호출해서 파라미터의 값을 입력(바인딩) 한다.
     * 예) 파라미터 이름이 username 이면 setUsername() 메서드를 찾아서 호출하면서 값을 입력한다.

     * 프로퍼티란?
     * 객체에 getUsername() , setUsername() 메서드가 있으면, 이 객체는 username 이라는 프로퍼티를 가지고 있다고 한다. (getXXX -> xxx)
     * username 프로퍼티의 값을 변경하면 setUsername() 이 호출되고, 조회하면 getUsername() 이 호출된다.
     * class HelloData {
     *  getUsername();
     *  setUsername();
     * }

     * 바인딩 오류
     * age=abc 처럼 숫자가 들어가야 할 곳에 문자를 넣으면 BindException 이 발생한다. 이런 바인딩 오류를 처리하는 방법은 검증 부분에서 다룬다.
    */
    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeV1(@ModelAttribute HelloData helloData) {
        log.info("username={}, age={}",
                helloData.getUsername(),
                helloData.getAge());
        return "ok";
    }

    /**
     * 1. @ModelAttribute 는 생략할 수 있다. (직접 만든 객체)
     * 2. @RequestParam 도 생략할 수 있으니 혼란이 발생할 수는 있다.
     * 3. argument resolver 로 지정해둔 타입 외 = @ModelAttribute

     * ArgumentResolver(아규먼트 리졸버)란?
     * 스프링의 디스패처 서블릿은 컨트롤러로 요청을 전달한다.
     * 그때 컨트롤러에서 필요로 하는 객체를 만들고 값을 바인딩하여 전달하기 위해 사용되는 것이 ArgumentResolver이다.
     * 스프링이 제공하는 다음과 같은 어노테이션들은 모두 ArgumentResolver로 동작한다.

     * @RequestParam: 쿼리 파라미터 값 바인딩
     * @ModelAttribute: 쿼리 파라미터 및 폼 데이터 바인딩
     * @CookieValue: 쿠키값 바인딩
     * @RequestHeader: 헤더값 바인딩
     * @RequestBody: 바디값 바인딩
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(),
                helloData.getAge());
        return "ok";
    }

}
