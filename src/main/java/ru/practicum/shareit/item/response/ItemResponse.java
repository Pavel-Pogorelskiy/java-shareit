package ru.practicum.shareit.item.response;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemResponse {
    Item get(Long id);

    List<Item> getAll(long userId);

    Item update(Long id, Item item, long idUser);

    Item save(Item item);

    List<Item> searchItem(String search);
}
