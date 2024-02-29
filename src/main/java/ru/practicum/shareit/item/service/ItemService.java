package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItem(Long id);

    List<ItemDto> getItemToUser(Long userId);

    ItemDto updateItem(ItemDto request, Long userId, Long itemId);

    ItemDto saveItem(ItemDto request, Long userId);

    void validationUser(Long id);

    List<ItemDto> searchItem(String search, Long userId);
}
