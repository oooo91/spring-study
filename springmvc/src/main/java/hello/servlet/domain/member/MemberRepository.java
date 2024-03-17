package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제 고려되어있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용을 고려한다.
 */
public class MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    private static final MemberRepository instance = new MemberRepository(); //싱글톤

    //무조건 얘로 조회하기
    public static MemberRepository getInstance() {
        return instance;
    }

    //생성자 private로 막기 (아무데서나 생성하지 못하게)
    private MemberRepository() {}

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
