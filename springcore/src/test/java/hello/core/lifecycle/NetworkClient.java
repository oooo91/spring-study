package hello.core.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class NetworkClient {
    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url : " + url);

        //connect();
        //call("초기화 연결 메시지");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //서비스 시작 시 호출
    public void connect() {
        System.out.println("connect : " + url);
    }

    public void call(String message) {
        System.out.println("call : " + url + " message : " + message);
    }

    //서비스 종료 시 호출
    public void disconnect() {
        System.out.println("close : " + url);
    }

    /*
    //의존관계 주입이 끝나면 호출해주겠다.
    @Override
    public void afterPropertiesSet() throws Exception {
        connect();
        call("초기화 연결 메시지");
    }

    //종료 직전에 호출해주겠다.
    @Override
    public void destroy() throws Exception {
      disconnect();
    }
    */

    @PostConstruct
    public void init() {
        System.out.println("networkclient.init");
        connect();
        call("초기화 연결 메시지");
    }

    @PreDestroy
    public void close() {
        System.out.println("networkclient.close");
        disconnect();
    }
}
