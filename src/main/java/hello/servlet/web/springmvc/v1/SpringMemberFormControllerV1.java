package hello.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @RequestMapping
 * 스프링은 애노테이션을 활용한 매우 유연하고, 실용적인 컨트롤러를 만들었는데 이것이 바로 @RequestMapping 애노테이션을 사용하는 컨트롤러이다.

 * 여담이지만 과거에는 스프링 프레임워크가 MVC 부분이 약해서 스프링을 사용하더라도 MVC 웹 기술은 스트럿츠 같은 다른 프레임워크를 사용했었다.
 * 그런데 @RequestMapping 기반의 애노테이션 컨트롤러가 등장하면서, MVC 부분도 스프링의 완승으로 끝이 났다.

 * @RequestMapping
 * RequestMappingHandlerMapping
 * RequestMappingHandlerAdapter
 * 앞서 보았듯이 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 RequestMappingHandlerMapping, RequestMappingHandlerAdapter 이다.
 * @RequestMapping 의 앞 글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는
 * 애노테이션 기반의 컨트롤러를 지원하는 핸들러 매핑과 어댑터이다.
 * 실무에서는 99.9% 이 방식의 컨트롤러를 사용한다.
 * 그럼 이제 본격적으로 애노테이션 기반의 컨트롤러를 사용해보자.
 * 지금까지 만들었던 프레임워크에서 사용했던 컨트롤러를 @RequestMapping 기반의 스프링 MVC 컨트롤러로 변경해보자

 * @Controller :
 * 스프링이 자동으로 스프링 빈으로 등록한다. (내부에 @Component 애노테이션이 있어서 컴포넌트 스캔의 대상이 됨)
 * 스프링 MVC에서 애노테이션 기반 컨트롤러로 인식한다.

 * @RequestMapping : 요청 정보를 매핑한다. 해당 URL이 호출되면 이 메서드가 호출된다.
 * 애노테이션을 기반으로 동작하기 때문에, 메서드의 이름은 임의로 지으면 된다.

 * ModelAndView : 모델과 뷰 정보를 담아서 반환하면 된다.
 * RequestMappingHandlerMapping 은 스프링 빈 중에서 @RequestMapping 또는 @Controller가 클래스 레벨에 붙어 있는 경우에 매핑 정보로 인식한다.
 */
@Controller //Component 포함 -> 빈  등록 + 핸들러 등록
@RequestMapping("/springmvc/v1/members")
public class SpringMemberFormControllerV1 {

    @RequestMapping("/new-form")
    public ModelAndView process() {
        return new ModelAndView("new-form");
    }
}
