package hello.typeconverterstart.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

//"127.0.0.1:8080" 문자 -> IpPort 객체로 바꾸기
@Getter
@EqualsAndHashCode
public class IpPort {

  private String ip;
  private int port;

  public IpPort(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }
}
