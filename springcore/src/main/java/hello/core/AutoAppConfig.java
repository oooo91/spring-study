package hello.core;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

/*
@Configuration //얘도 @Component 붙어있다
@ComponentScan(
        basePackages = "hello.core.member", //여기서부터 하위 파일만 찾아가서 member만 조회됨
        basePackageClasses = AutoAppConfig.class, //클래스를 지정할 수 있음, //이 base들을 정하지 않는다면 default로 간다 -> 현재 @ComponentScan이 붙은 설정 정보 패키지가 시작 위치가 된다 (hello.core) -> 따라서 ComponentScan 붙은 클래스를 최상단에 놔두는 게 관례다
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
                classes = Configuration.class
        )) //그간 각 메소드마다 @Bean을 통해 등록했는데, 얘는 @Component 붙인 애들을 다 컨테이너에 등록해줌, 저거 왜 빼냐면 AppConfig < 수동으로 @Bean등록한 클래스 제외하려고
*/
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class))
public class AutoAppConfig {

        //컴포넌트 스캔의 용도뿐 아니라 다음 어노테이션이 있으면 스프링은 부가 기능을 수행함
        //@Controller -> 스프링 MVC 컨트롤러로 인식
        //@Repository -> 스프링 데이터 접근 계층으로 인식하고 데이터 계층의 예외를 스프링 예외로 변환
        //@Configuration -> 앞서 보았듯이 스프링 설정 정보로 인식하고 스프링 빈이 싱글톤을 유지하도록 추가 처리함
        //@Service -> 개발자들이 핵심 비즈니스 로직이 여기있구나.. 인식하는 용도

        //같은 이름이 빈으로 등록될 때 어떻게 될까
        //자동 빈 vs 자동 빈 충
        //
        // 돌할 경우는 컴파일 오류가 난다
        //아래는 자동 빈 vs 수동 빈 충돌이다
        //수동 빈 등록이 우선권을 가져서 수동 빈이 자동 빈을 오버라이딩을 해버린다. 그래서 덮어씌우면서 등록되는데, 스프링 부트는 이를 막아놔서 스프링에서 오류를 준다.
        //@Bean(name = "memoryMemberRepository")
        //MemberRepository memberRepository() {
        //        return new MemoryMemberRepository();
        //}
}
