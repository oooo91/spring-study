package hello.servlet.web.springmvc;

public class intro {
    /**
     * 스프링은 그간의 구조와 동일하되 frontController가 아닌 DispatcherServlet이 동작한다.
     * DispatcherServlet
     * ㄴ 얘도 서블릿 (DispatcherServlet -> FrameworkServlet -> HttpServletBean -> HttpServlet)
     * ㄴ 스프링부트가 얘를 서블릿으로 자동 등록을 하면서 모든 경로(urlPatterns="/")(우선순위 가장 낮음)에 대해서 매핑한다.
     * ㄴ 더 자세한 경로가 우선순위가 높기 때문에 기존에 등록된 서블릿도 함께 동작한다.
     *
     *
     * 요청 흐름
     * 서블릿 객체 호출 -> service() 호출 (DispatchServlet이 service()를 오버라이딩하여
     * FrameworkServlet.service() 호출 시작으로 DispatcherServlet의 doDispatch()가 호출된다.
     * (얘가 그간의 frontController 작업을 다 처리한다. handler, handlerAdapter,해당 controller을 찾고 modelAndView 받아와서 view 이름으로 진짜 view로 랜더링한다.)
     *
     * 1. 핸들러 조회: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
     * 2. 핸들러 어댑터 조회: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
     * 3. 핸들러 어댑터 실행: 핸들러 어댑터를 실행한다.
     * 4. 핸들러 실행: 핸들러 어댑터가 실제 핸들러를 실행한다.
     * 5. ModelAndView 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서 반환한다.
     * 6. viewResolver 호출: 뷰 리졸버를 찾고 실행한다.
     * JSP의 경우: InternalResourceViewResolver 가 자동 등록되고, 얘가 사용된다.
     * 7. View 반환: 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를 반환한다.
     * JSP의 경우 InternalResourceView(JstlView) 를 반환하는데, 내부에 forward() 로직이 있다.
     * 8. 뷰 렌더링: 뷰를 통해서 뷰를 렌더링 한다.
     *
     *
     * 인터페이스 살펴보기
     * 스프링 MVC의 큰 강점은 DispatcherServlet 코드의 변경 없이, 원하는 기능을 변경하거나 확장할 수 있다는 점이다.
     * 지금까지 설명한 대부분을 확장 가능할 수 있게 인터페이스로 제공한다.
     * 이 인터페이스들만 구현해서 DispatcherServlet 에 등록하면 여러분만의 컨트롤러를 만들 수도 있다.
     *
     *
     * 주요 인터페이스 목록
     * 핸들러 매핑: org.springframework.web.servlet.HandlerMapping
     * 핸들러 어댑터: org.springframework.web.servlet.HandlerAdapter
     * 뷰 리졸버: org.springframework.web.servlet.ViewResolver
     * 뷰: org.springframework.web.servlet.View
     */
}
