package hello.core.scan.filter;

import java.lang.annotation.*;

//includeFilters -> 컴포넌트 스캔 대상을 추가로 지정한다
//excludeFilters -> 컴퓨넌트 스캔에서 제외할 대상을 지정한다
//아래는 컴포넌트 스캔 대상에 제외할 어노테이션
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {

}
