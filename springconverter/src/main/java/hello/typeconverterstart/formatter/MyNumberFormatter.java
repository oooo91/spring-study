package hello.typeconverterstart.formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

  @Override
  public Number parse(String text, Locale locale) throws ParseException {
    log.info("text={}, locale={}", text, locale);
    //"1,000" -> 1000
    return NumberFormat.getInstance(locale).parse(text);
  }

  //ctrl + alt + n -> 코드 합치기
  @Override
  public String print(Number object, Locale locale) {
    log.info("object={}, locale={}", object, locale);
    return NumberFormat.getInstance(locale).format(object);
  }
}
