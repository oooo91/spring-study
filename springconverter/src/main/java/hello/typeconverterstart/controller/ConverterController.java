package hello.typeconverterstart.controller;

import hello.typeconverterstart.type.IpPort;
import javax.swing.FocusManager;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConverterController {

  //controller 요청일 때도 conversionService 사용되고 (String -> 객체)
  //템플릿을 요청할 때도 사용된다. (객체 -> String)
  @GetMapping("/converter-view")
  public String converterView(Model model) {

    model.addAttribute("number", 10000);
    model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
    return "converter-view";
  }

  //form test
  @GetMapping("/converter/edit")
  public String converterForm(Model model) {
    IpPort ipPort = new IpPort("127.0.0.1", 8080);
    Form form = new Form(ipPort);
    model.addAttribute("form", form);
    return "converter-form";
  }

  //@ModelAttribute -> String 을 IpPort 으로 타입 변환
  @PostMapping("/converter/edit")
  public String converterEdit(@ModelAttribute Form form, Model model) {
    IpPort ipPort = form.getIpPort();
    model.addAttribute("ipPort", ipPort);
    return "converter-view";
  }

  @Data
  static class Form {
    private IpPort ipPort;

    public Form(IpPort ipPort) {
      this.ipPort = ipPort;
    }
  }
}
