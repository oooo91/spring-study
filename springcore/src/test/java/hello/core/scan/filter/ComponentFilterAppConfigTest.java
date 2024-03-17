package hello.core.scan.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentFilterAppConfigTest {

    @Test
    void filterScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        BeanA beanA = ac.getBean("beanA", BeanA.class); //얘는 있음
        //BeanB beanB = ac.getBean("beanB", BeanB.class); //얘는 빈으로 등록 안됨
        assertThat(beanA).isNotNull();
        assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("beanB", BeanB.class));
    }

    //FilterType 옵션
    //ANNOTATION : 기본값, 생략 가능, 어노테이션을 인식해서 동작함
    //ASSIGNABLE_TYPE : 지정한 타입과 자식 타입을 인식해서 동작함
    //ASPECTJ : AspectJ 패턴을 사용
    //REGEX : 정규 표현식
    //CUSTOM : TypeFilter 이라는 인터페이스를 구현하여 처리

    @Configuration
    @ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
                    classes = MyIncludeComponent.class),
                    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
                    classes = MyExcludeComponent.class))
    static class ComponentFilterAppConfig {

    }
}
