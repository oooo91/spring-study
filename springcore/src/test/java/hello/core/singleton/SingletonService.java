package hello.core.singleton;

public class SingletonService {
    //자기 자신을 내부에 private static 으로 선언 -> 클래스 레벨에 올라가므로 하나만 존재
    //static 영역에 객체를 딱 1개만 생성함 -> 자바가 뜰 때 딱 하나의 SingletonService 생성함
    private static final SingletonService instance = new SingletonService();

    //이 메서드를 통해서만 조회 가능 (같은 객체 조회)
    public static SingletonService getInstance() {
        return instance;
    }

    //하나만 필요하기 때문에 생성자를 private으로 선언해서 다른 클래스에서 new로 생성 못하게 막기
    private SingletonService() {}

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
