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
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return new ModelAndView("new-form"); //논리 이름 -> 어케 물리 주소로 바꾸려나
    }

    /**
     * 스프링 mvc의 핸들러 매핑, 어뎁터란?
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

    /**
     * 스프링 mvc의 뷰 리졸버란?
     * return new ModelAndView("new-form"); //논리 이름 -> 어케 물리 주소로 바꾸려나

     * 뷰 리졸버 - InternalResourceViewResolver
     * 스프링 부트는 InternalResourceViewResolver 라는 뷰 리졸버를 자동으로 등록하는데
     * 이때 application.properties 에 등록한 spring.mvc.view.prefix , spring.mvc.view.suffix 설정 정보를 사용해서 등록한다.
     * 참고로 권장하지는 않지만 설정 없이 다음과 같이 전체 경로를 주어도 동작하기는 한다.
     * return new ModelAndView("/WEB-INF/views/new-form.jsp");

     * spring.mvc.view.prefix=/WEB-INF/views
     * spring.mvc.view.suffix=.jsp

     1 = BeanNameViewResolver : 빈 이름으로 뷰를 찾아서 반환한다. (예: 엑셀 파일 생성 기능에 사용)
     2 = InternalResourceViewResolver : JSP를 처리할 수 있는 뷰를 반환한다.

     1. 핸들러 어댑터 호출
     핸들러 어댑터를 통해 new-form 이라는 논리 뷰 이름을 획득한다.

     2. ViewResolver 호출
     new-form 이라는 뷰 이름으로 viewResolver를 순서대로 호출한다.
     BeanNameViewResolver 는 new-form 이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없다.
     InternalResourceViewResolver 가 호출된다.

     3. InternalResourceViewResolver
     이 뷰 리졸버는 InternalResourceView 를 반환한다.

     4. 뷰 - InternalResourceView
     InternalResourceView 는 JSP처럼 포워드 forward() 를 호출해서 처리할 수 있는 경우에 사용한다. (ModelView와 같다)

     5. view.render()
     view.render() 가 호출되고 InternalResourceView 는 forward() 를 사용해서 JSP를 실행한다.
     > 참고
     > InternalResourceViewResolver 는 만약 JSTL 라이브러리가 있으면 InternalResourceView 를
     상속받은 JstlView 를 반환한다. JstlView 는 JSTL 태그 사용시 약간의 부가 기능이 추가된다.
     > 참고
     > 다른 뷰는 실제 뷰를 렌더링하지만, JSP의 경우 forward() 통해서 해당 JSP로 이동(실행)해야 렌더링이 된다.
     > JSP를 제외한 나머지 뷰 템플릿들은 forward() 과정 없이 바로 렌더링 된다.
     > 참고
     > Thymeleaf 뷰 템플릿을 사용하면 ThymeleafViewResolver 를 등록해야 한다. 최근에는 라이브러리만 추가하면 스프링 부트가 이런 작업도 모두 자동화해준다
     */
}
