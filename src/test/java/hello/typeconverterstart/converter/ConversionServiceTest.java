package hello.typeconverterstart.converter;

import static org.assertj.core.api.Assertions.*;

import hello.typeconverterstart.type.IpPort;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

public class ConversionServiceTest {

  @Test
  void conversionService() {
    //등록
    DefaultConversionService conversionService = new DefaultConversionService();
    conversionService.addConverter(new StringToIntegerConverter());
    conversionService.addConverter(new IntegerToStringConverter());
    conversionService.addConverter(new StringToIpPortConverter());
    conversionService.addConverter(new IpPortToStringConverter());

    //사용
    //"10"을 Integer.class 타입으로 변환하고 싶다 -> conversionService 가 자동으로 StringToIntegerConverter 을 사용하여 변환해준다.
    //conversionService 장점 -> 등록과 사용 분리 (사용하는 입장에서는 뭐가 등록됐는지 몰라도 된다.)
    //스프링을 보면 대부분의 기능 설계가 사용과 등록이 분리가 되어있다. (참고)
    //스프링은 내부에서 이러한 ConversionService 를 사용하여 타입을 변환한다. 가령 @RequestParam 같은 곳에서 이 기능을 사용하여 타입을 변환한다.
    assertThat(conversionService.convert("10", Integer.class)).isEqualTo(10);
    assertThat(conversionService.convert(10, String.class)).isEqualTo("10");
    assertThat(conversionService.convert("127.0.0.1:8080", IpPort.class)).isEqualTo(new IpPort("127.0.0.1", 8080));
    assertThat(conversionService.convert(new IpPort("127.0.0.1", 8080), String.class)).isEqualTo("127.0.0.1:8080");
  }

}
