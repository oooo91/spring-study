package hello.core.singleton;

public class StatefulService {
    /* 문제 발생
    private int price; //상태를 유지하는 필드 -> 이지만 싱글톤이라 공유되는 필드됨

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price; //여기가 문제! -> 필드값 바뀜 (10000 -> 20000)
    }

    public int getPrice() {
        return price;
    } */

    //따라서 특정 클라이언트에 의존적인 필드가 있으면 안됨 (값을 변경할 수 있게 하면 안됨)
    //가급적 읽기만 가능하게 해야함 -> 필드 대신에 자바에서 공유되지 않는 지역변수, 파라미터, ThreadLocal 등을 사용해야함
    //그래서 상태를 유지하는 상태(stateful)가 아니라 무상태(stateless)로 설계해야한다

    public int order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        return price; //걍 넘겨
    }
}
