package hello.typeconverterstart;

import hello.typeconverterstart.converter.IntegerToStringConverter;
import hello.typeconverterstart.converter.IpPortToStringConverter;
import hello.typeconverterstart.converter.StringToIntegerConverter;
import hello.typeconverterstart.converter.StringToIpPortConverter;
import hello.typeconverterstart.formatter.MyNumberFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    //registry.addConverter(new StringToIntegerConverter());
    //registry.addConverter(new IntegerToStringConverter());
    registry.addConverter(new StringToIpPortConverter());
    registry.addConverter(new IpPortToStringConverter());
    //추가
    registry.addFormatter(new MyNumberFormatter());
  }
}
