package hello.typeconverterstart.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

//문제는 Convert 상속해도 결국 개발자가 직접 타입 변환 코드를 작성해야한다.
//자동화는 없을까?
@Slf4j
public class IntegerToStringConverter implements Converter<Integer, String> {

  @Override
  public String convert(Integer source) {
    log.info("convert source={}", source);
    return String.valueOf(source);
  }
}
