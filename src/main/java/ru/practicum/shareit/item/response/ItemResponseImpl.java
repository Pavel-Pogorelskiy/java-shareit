package ru.practicum.shareit.item.response;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AnotherUserException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemResponseImpl implements ItemResponse {
    private Map<Long, Item> itemsStorage = new HashMap<>();

    @Override
    public Item get(Long id) {
        return itemsStorage.get(id);
    }

    @Override
    public List<Item> getAll(long userId) {
        return itemsStorage.values().stream()
                .filter(o -> o.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Long id, Item item, long idUser) {
        Item oldItem = itemsStorage.get(id);
        if (oldItem.getOwner().getId() != idUser) {
            throw new AnotherUserException("Пользователь с id = " + idUser + " не имеет права " +
                    "на изменение вещи, так как не является ее владельцем");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        itemsStorage.put(id, oldItem);
        return oldItem;
    }

    @Override
    public Item save(Item item) {
        itemsStorage.put(item.getId(),item);
        return item;
    }

    @Override
    public List<Item> searchItem(String search) {
        return itemsStorage.values().stream()
                .filter(it -> (it.getName().toLowerCase().contains(search.toLowerCase())
                        || it.getDescription().toLowerCase().contains(search.toLowerCase()))
                        && it.getAvailable() == true && !search.isBlank())
                .collect(Collectors.toList());
    }
}
