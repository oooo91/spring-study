package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 예전에는 스프링 mvc 컨트롤러를 어떻게 만들었나
 * 아래의 Controller와 @Controller은 전혀 다른 코드다.
 * 핸들러 매핑을 따로 처리해야한다.
 */
@Component("/springmvc/old-controller") //url이자 스프링 빈 이름
public class OldController implements Controller {

    /**
     http://localhost:8080/springmvc/old-controller 실행 시 콘솔에 OldController.handleRequest 이 출력되면 성공이다.
     이 컨트롤러는 어떻게 호출될 수 있었을까?

     컨트롤러가 호출되려면 다음 2가지가 필요하다.

     1. HandlerMapping(핸들러 매핑)
     핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 한다.
     예) 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑이 필요하다.

     HandlerAdapter(핸들러 어댑터)
     핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요하다.
     예) Controller 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 한다.

     스프링은 이미 필요한 핸들러 매핑과 핸들러 어댑터를 대부분 구현해두었다. 개발자가 직접 핸들러 매핑과 핸들러 어댑터를 만드는 일은 거의 없다.
     스프링 부트는 자동으로 등록하는 여러가지 핸들러 매핑과 핸들러 어댑터가 있다.
     ex ) 0순위 : 어노테이션 기반의 핸들러 매핑이다. @RequestMapping 위주로 이름을 찾는다. 핸들러를 못찾으면 BeanNameUrlHandlerMapping으로 찾는다.

     HandlerMapping
     0 = RequestMappingHandlerMapping : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
     1 = BeanNameUrlHandlerMapping : 스프링 빈의 이름으로 핸들러를 찾는다.

     HandlerAdapter
     0 = RequestMappingHandlerAdapter : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
     1 = HttpRequestHandlerAdapter : HttpRequestHandler 처리
     2 = SimpleControllerHandlerAdapter : Controller 인터페이스(애노테이션X, 과거에 사용) 처리
     */

    /**
     즉 해당 클래스는 이런 흐름으로 동작한다.

     1. 핸들러 매핑으로 핸들러 조회
     1. HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
     2. 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는
     BeanNameUrlHandlerMapping 가 실행에 성공하고 핸들러인 OldController 를 반환한다.

     2. 핸들러 어댑터 조회
     1. HandlerAdapter 의 supports() 를 순서대로 호출한다.
     2. SimpleControllerHandlerAdapter 가 Controller 인터페이스를 지원하므로 대상이 된다.
     3. 핸들러 어댑터 실행

     1. 디스패처 서블릿이 조회한 SimpleControllerHandlerAdapter 를 실행하면서 핸들러 정보도 함께 넘겨준다.
     2. SimpleControllerHandlerAdapter 는 핸들러인 OldController 를 내부에서 실행하고, 그 결과를 반환한다.

     정리 - OldController 핸들러매핑, 어댑터
     OldController 를 실행하면서 사용된 객체는 다음과 같다.
     HandlerMapping = BeanNameUrlHandlerMapping
     HandlerAdapter = SimpleControllerHandlerAdapter
     */

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return null;
    }
}
