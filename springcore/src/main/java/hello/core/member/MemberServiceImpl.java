package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService {
    //private final MemberRepository memberRepository = new MemoryMemberRepository(); -> app config가 대신해줌
    //구현체에 뭐가 들어갈지는 생성자를 통해서 받는다. 그럼 서비스 입장에서는 구현체 몰라도 됨 (의존하지 않음) -> DIP 지킴
    private final MemberRepository memberRepository;

    //@Component 쓰면 의존관계는 어떻게 함? (AppConfig 사용할 때는 @Bean으로 직접 설정 정보를 작성했고 의존관계도 명시했다)
    //@Autowired를 사용하여 의존관계를 정립한다

    //@Component로 등록되는 빈 이름은 클래스명을 사용함 (앞글자는 소문자로), 빈 이름 직접 지정하고 싶으면 @Component("직접 지정한 이름")
    //@Autowired는 어떻게 등록되냐? -> 클래스 생성하면서 memberRepository가 주입될 텐데,
    // MemberRepository 타입으로 이 객체를 찾아서 컨테이너가 알아서 주입해준다 (타입이 같은 애를 찾아 주입한다)
    //만일 같은 타입이 있으면? -> 나중에 알려준대
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository) { //생성자를 통해 주입된다
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
