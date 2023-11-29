package hello.typeconverterstart.controller;

import hello.typeconverterstart.type.IpPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/hello-v1")
  public String helloV1(HttpServletRequest request) {
    String data = request.getParameter("data"); //문자 타입 조회
    Integer intValue = Integer.valueOf(data); //숫자 타입으로 변경
    System.out.println("intValue = " + intValue);
    return "ok";
  }

  //StringToIntegerConverter 사용된다.
  //MyNumberFormatter 등록했다 -> data 로 "10,000" 오면 data=10000 이 출력된다.
  @GetMapping("/hello-v2")
  public String helloV2(@RequestParam Integer data) {
    System.out.println("data = " + data);
    return "ok";
  }

  //StringToIpPortConverter 사용된다.
  //실제 @RequestParam 사용하는 Resolver 가 ConversionService 를 사용한다.
  @GetMapping("/ip-port")
  public String ipPort(@RequestParam IpPort ipPort) {
    System.out.println("ipPort = " + ipPort.getIp());
    System.out.println("ipPort.getPort() = " + ipPort.getPort());
    return "ok";
  }
}
