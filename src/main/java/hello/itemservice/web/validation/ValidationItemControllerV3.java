package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

  private final ItemRepository itemRepository;

  @GetMapping
  public String items(Model model) {
    List<Item> items = itemRepository.findAll();
    model.addAttribute("items", items);
    return "validation/v3/items";
  }

  @GetMapping("/{itemId}")
  public String item(@PathVariable long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "validation/v3/item";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("item", new Item());
    return "validation/v3/addForm";
  }

  /*
  @Validated -> 검증기 뺐는데도 알아서 잘 된다. (Item 객체의 validation 이 적용된다.)
  spring-boot-starter-validation 라이브러리 넣으면 -> LocalValidatorFactoryBean 을 글로벌 Validator 로 등록한다.
  LocalValidatorFactoryBean 은 @Validated 적용된 객체를 검증한다. (@NotNull 같은 어노테이션을 보고 검증을 수행한다.)
  타입 변환 성공한 (바인딩에 성공한) 것들만 검증 과정을 거질 수 있다.
  Bean Validation 이 기본적으로 적용하는 오류 메시지 제공한다 -> 커스텀은? 어노테이션 이름으로 오류 코드가 등록된다. 해당 오류 코드에 원하는 메시지 매치만 하면 된다.
   */
  //@PostMapping("/add")
  public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult,
      RedirectAttributes redirectAttributes, Model model, FieldError error) {

    //특정 필드가 아닌 복합 룰 검증 (Object)
    if (item.getPrice() != null && item.getQuantity() != null) {
      int resultPrice = item.getPrice() * item.getQuantity();
      if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
      }
    }

    if (bindingResult.hasErrors()) {
      log.info("errors={} ", bindingResult);
      return "validation/v3/addForm";
    }

    //성공
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v3/items/{itemId}";
  }

  //Validated는 save 조건일 때만 검증하도록 한다.
  @PostMapping("/add")
  public String addItem2(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult,
      RedirectAttributes redirectAttributes, Model model, FieldError error) {

    //특정 필드가 아닌 복합 룰 검증 (Object)
    if (item.getPrice() != null && item.getQuantity() != null) {
      int resultPrice = item.getPrice() * item.getQuantity();
      if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
      }
    }

    if (bindingResult.hasErrors()) {
      log.info("errors={} ", bindingResult);
      return "validation/v3/addForm";
    }

    //성공
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v3/items/{itemId}";
  }

  @GetMapping("/{itemId}/edit")
  public String editForm(@PathVariable Long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "/validation/v3/editForm";
  }

  //@PostMapping("/{itemId}/edit")
  public String edit(@PathVariable Long itemId, @Validated @ModelAttribute Item item,
      BindingResult bindingResult) {

    //특정 필드가 아닌 복합 룰 검증 (Object)
    if (item.getPrice() != null && item.getQuantity() != null) {
      int resultPrice = item.getPrice() * item.getQuantity();
      if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
      }
    }

    if (bindingResult.hasErrors()) {
      log.info("errors={} ", bindingResult);
      return "validation/v3/editForm";
    }

    itemRepository.update(itemId, item);
    return "redirect:/validation/v3/items/{itemId}";
  }

  @PostMapping("/{itemId}/edit")
  public String edit2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item,
      BindingResult bindingResult) {

    //특정 필드가 아닌 복합 룰 검증 (Object)
    if (item.getPrice() != null && item.getQuantity() != null) {
      int resultPrice = item.getPrice() * item.getQuantity();
      if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
      }
    }

    if (bindingResult.hasErrors()) {
      log.info("errors={} ", bindingResult);
      return "validation/v3/editForm";
    }

    itemRepository.update(itemId, item);
    return "redirect:/validation/v3/items/{itemId}";
  }
}

