package hello.core.member;

public class MemberServiceImpl implements MemberService {
    //private final MemberRepository memberRepository = new MemoryMemberRepository(); -> app config가 대신해줌
    //구현체에 뭐가 들어갈지는 생성자를 통해서 받는다. 그럼 서비스 입장에서는 구현체 몰라도 됨 (의존하지 않음) -> DIP 지킴
    private final MemberRepository memberRepository;

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
