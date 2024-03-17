package hello.core.beanfind;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    //등록된 빈 모두 출력
    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames(); //name(KEY)을 배열로 꺼내기
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName); //name으로 등록된 Bean 꺼내기
            System.out.println("name = " + beanDefinitionName + " object = " + bean);
        }
    }

    @Test
    @DisplayName("내가 등록한 빈 출력하기")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames(); //name을 배열로 꺼내기
        for (String beanDefinitionName : beanDefinitionNames) {
           BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName); //이름으로 bean에 대한 meta 정보 꺼내기

           //Role ROLE_APPLICATION : 직접 등록한 애플리케이션 빈
           //Role ROLE_INFRASTRUCTURE : 스프링이 내부에서 사용하는 빈
           if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) { //ROLE_APPLICATION -> 스프링이 자동으로 주입한 게 아니라 내가 만든 빈이라면
               Object bean = ac.getBean(beanDefinitionName); //name으로 등록된 Bean 꺼내기
               System.out.println("name = " + beanDefinitionName + " object = " + bean);
           }
        }
    }
}
