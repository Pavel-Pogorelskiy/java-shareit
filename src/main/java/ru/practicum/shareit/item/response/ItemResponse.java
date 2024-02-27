package ru.practicum.shareit.item.response;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemResponse {
    Item get(Long id);

    List<Item> getAll(Long userId);

    Item uptade(Long id, Item item, Long userId);

    Item save(Item item);

    List<Item> searchItem(String search);
}
