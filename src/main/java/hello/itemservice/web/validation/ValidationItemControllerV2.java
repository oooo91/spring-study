package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

  private final ItemRepository itemRepository;

  @GetMapping
  public String items(Model model) {
    List<Item> items = itemRepository.findAll();
    model.addAttribute("items", items);
    return "validation/v2/items";
  }

  @GetMapping("/{itemId}")
  public String item(@PathVariable long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "validation/v2/item";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("item", new Item());
    return "validation/v2/addForm";
  }

  /*
  BindingResult
  Item에서 바인딩 안 된 결과값이 해당 객체에 바인딩, 메시지 전달, @ModelAttribute 다음에 작성되어야한다
  타입 오류 시 Controller 접근 못하는데 BindingResult 있으면 오류 있더라도 접근 가능하다 (오류 처리하니까)
  Errors -> BindingResult -> BeanPropertyBindingResult 가 구현하고 있어서 결국 Errors 로 받아도 된다 *아하
  대신 Errors는 오류 저장/조회 기능을 제공하고 BindingResult는 추가적인 기능을 제공하므로 BindingResult 많이 쓰인다 ex) addError()

  @PostMapping("/add")
  public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult,
      RedirectAttributes redirectAttributes, Model model) {

    //검증 오류 결과
    Map<String, String> errors = new HashMap<>();
    if (!StringUtils.hasText(item.getItemName())) {
      bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
    }
    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
      errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
      bindingResult.addError(
          new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
    }
    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
      bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));
    }

    //특정 필드가 아닌 복합 룰 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
      int resultPrice = item.getPrice() * item.getQuantity();
      if (resultPrice < 10000) {
        bindingResult.addError(new ObjectError("item",
            "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
      }
    }

    //!errors.isEmpty() -> bindingResult.hasErrors() //clean
    if (bindingResult.hasErrors()) {
      //model.addAttribute("errors", errors); BindingResult -> 스프링이 알아서 model 에 담는다.
      return "validation/v2/addForm";
    }

    //성공
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v2/items/{itemId}";
  }
   */

//@PostMapping("/add")
//public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult,
//    RedirectAttributes redirectAttributes, Model model, FieldError error) {
//
//    Map<String, String> errors = new HashMap<>();
//    if (!StringUtils.hasText(item.getItemName())) {
//        //bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
//
//        //rejectValue 추가 -> 페이지로 돌아가도 값 유지, bindingFailure -> 타입 바인딩 오류
//        //FieldError -> code, arguments 파라미터 -> 에러 코드, 메시지 공통화 가능 -> 오류 발생 시 오류 코드로 메시지 찾기 위해 사용
//        //FieldError, ObjectError 생성자 -> errorCode, arguments 제공한다.
//        bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
//            false, null, null, "상품 이름은 필수입니다."));
//
//    }
//    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//        bindingResult.addError(new FieldError("item", "price", item.getPrice(),
//            false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
//
//    }
//    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
//        bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
//            false, null, null, "수량은 최대 9,999까지 허용합니다."));
//    }
//
//    //특정 필드가 아닌 복합 룰 검증
//    if (item.getPrice() != null && item.getQuantity() != null) {
//        int resultPrice = item.getPrice() * item.getQuantity();
//        if (resultPrice < 10000) {
//            bindingResult.addError(new ObjectError("item", null, null,
//                "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
//        }
//    }
//
//    //!errors.isEmpty() -> bindingResult.hasErrors() //clean
//    if (bindingResult.hasErrors()) {
//        //model.addAttribute("errors", errors); BindingResult -> 스프링이 알아서 model 에 담는다.
//        return "validation/v2/addForm";
//    }
//
//    //성공
//    Item savedItem = itemRepository.save(item);
//    redirectAttributes.addAttribute("itemId", savedItem.getId());
//    redirectAttributes.addAttribute("status", true);
//    return "redirect:/validation/v2/items/{itemId}";
//  }

//  @PostMapping("/add")
//  public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult,
//      RedirectAttributes redirectAttributes, Model model, FieldError error) {
//
//    Map<String, String> errors = new HashMap<>();
//    if (!StringUtils.hasText(item.getItemName())) {
//      //bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
//
//      //rejectValue 추가 -> 페이지로 돌아가도 값 유지, bindingFailure -> 타입 바인딩 오류
//      //FieldError -> code, arguments 파라미터 -> 에러 코드, 메시지 공통화 가능 -> 오류 발생 시 오류 코드로 메시지 찾기 위해 사용
//      //FieldError, ObjectError 생성자 -> errorCode, arguments 제공한다.
//      //사실 objectName 안 넣어줘도 FieldError는 target 정보를 다 알고있다. -> rejectValue(), reject() 를 사용해서 FieldError, ObjectError 를 직접 사용하지 않고도 에러 처리를 할 수 있다.
//      log.info("objectName={}", bindingResult.getObjectName());
//      log.info("target={}", bindingResult.getTarget());
//
//      bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
//          false, new String[]{"required.item.itemName"}, null, null));
//
//    }
//    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//      bindingResult.addError(new FieldError("item", "price", item.getPrice(),
//          false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
//
//    }
//    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
//      bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
//          false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
//    }
//
//    //특정 필드가 아닌 복합 룰 검증
//    if (item.getPrice() != null && item.getQuantity() != null) {
//      int resultPrice = item.getPrice() * item.getQuantity();
//      if (resultPrice < 10000) {
//        bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
//      }
//    }
//
//    //!errors.isEmpty() -> bindingResult.hasErrors() //clean
//    if (bindingResult.hasErrors()) {
//      //model.addAttribute("errors", errors); BindingResult -> 스프링이 알아서 model 에 담는다.
//      return "validation/v2/addForm";
//    }
//
//    //성공
//    Item savedItem = itemRepository.save(item);
//    redirectAttributes.addAttribute("itemId", savedItem.getId());
//    redirectAttributes.addAttribute("status", true);
//    return "redirect:/validation/v2/items/{itemId}";
//  }

  @PostMapping("/add")
  public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult,
      RedirectAttributes redirectAttributes, Model model, FieldError error) {

    Map<String, String> errors = new HashMap<>();
    if (!StringUtils.hasText(item.getItemName())) {
      //bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));

      //rejectValue 추가 -> 페이지로 돌아가도 값 유지, bindingFailure -> 타입 바인딩 오류
      //FieldError -> code, arguments 파라미터 -> 에러 코드, 메시지 공통화 가능 -> 오류 발생 시 오류 코드로 메시지 찾기 위해 사용
      //FieldError, ObjectError 생성자 -> errorCode, arguments 제공한다.
      //사실 objectName 안 넣어줘도 FieldError는 target 정보를 다 알고있다. -> rejectValue(), reject() 를 사용해서 FieldError, ObjectError 를 직접 사용하지 않고도 에러 처리를 할 수 있다.
      log.info("objectName={}", bindingResult.getObjectName());
      log.info("target={}", bindingResult.getTarget());

      //bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
      //    false, new String[]{"required.item.itemName"}, null, null));
      bindingResult.rejectValue("itemName", "required"); //->require만 써도 내부 규칙에 따라 required.item.itemName을 찾는다.

    }
    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
      bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
    }
    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
      bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
    }

    //특정 필드가 아닌 복합 룰 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
      int resultPrice = item.getPrice() * item.getQuantity();
      if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
      }
    }

    //!errors.isEmpty() -> bindingResult.hasErrors() //clean
    if (bindingResult.hasErrors()) {
      //model.addAttribute("errors", errors); BindingResult -> 스프링이 알아서 model 에 담는다.
      return "validation/v2/addForm";
    }

    //성공
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v2/items/{itemId}";
  }


  @GetMapping("/{itemId}/edit")
  public String editForm(@PathVariable Long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "/validation/v2/editForm";
  }

  @PostMapping("/{itemId}/edit")
  public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
    itemRepository.update(itemId, item);
    return "redirect:/validation/v2/items/{itemId}";
  }

}

