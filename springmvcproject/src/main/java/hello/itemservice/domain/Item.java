package hello.itemservice.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Data = Getter, Setter, RequiredArgsConstructor, ToString, EqualsAndHashCode 자동 생성 -> 위험;
 */
@Getter
@Setter
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {

    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
