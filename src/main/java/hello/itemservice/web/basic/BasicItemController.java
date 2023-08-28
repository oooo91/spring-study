package hello.itemservice.web.basic;

import hello.itemservice.domain.Item;
import hello.itemservice.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor //final 붙은 필드 기반 생성자 만듦
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

   // @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);
        model.addAttribute("item", item);

        return "basic/item";
    }

    /** ㅁㅊ
     * ModelAttribute -> 요청 파라미터 처리할 뿐 아니라 model.addAttribute("item", item) 자동으로 만들어준다.
     */
    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) {

        itemRepository.save(item);
        //model.addAttribute("item", item); 생략 가능

        return "basic/item";
    }

    /**
     * ("") 생략하면 -> 클래스 이름 맨 앞의 글자를 소문자로 바꾼 키로 ModelAttribute에 담긴다.
     * 심지어 ModelAttribute 생략 가능하다.
     */
    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {

        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * 심지어 ModelAttribute 생략 가능하다.
     */
    @PostMapping("/add")
    public String addItemV4(Item item) {

        itemRepository.save(item);
        return "basic/item";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    /**
     * 왜 수정할 때는 리다이렉트를 쓰나?
     * 이슈 -> 마지막 요청이 post(등록)이라, 새로고침 시 마지막 행위가 다시 요청되어 계속 등록되는 현상
     * 웹 브라우저의 새로 고침은 마지막에 서버에 전송한 데이터를 다시 전송한다.
     * 이때 post 요청 이후 리다이렉트로 상품 조회를 보내버리면, 새로고침을 하더라도 마지막 요청이 조회이기 때문에 위의 현상이 해결된다.
     * 이럴 때 리다이렉트가 사용된다 -> prg (post redirect get 라고 한다.)
     */
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
       itemRepository.update(itemId, item);
       return "redirect:/basic/items/{itemId}"; //@PathVariable Long itemId의 값을 그대로 사용한다.
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
