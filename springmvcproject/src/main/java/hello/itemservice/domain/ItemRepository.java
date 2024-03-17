package hello.itemservice.domain;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    //멀티스레드 안됨, 사용할 거면 ConcurrentHashMap 사용해야함, Long도 하면 안됨 동시에 접근돼서 꼬일 수 있다.
    private static final Map<Long, Item> store = new HashMap<>(); //static
    private static long sequence = 0L; //static

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    //중복이냐 명확성이냐 -> 중복이 있떠라도 명확성을 추구해야됨, 사실 id 안쓸거면 Item보다 따로 ItemParamDto 등 따로 만드는 게 좋다.
    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
