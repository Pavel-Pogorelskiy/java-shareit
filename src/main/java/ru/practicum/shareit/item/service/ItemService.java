package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItem(Long id, Long userId);

    List<ItemDto> getItemToUser(Long userId);

    ItemDto updateItem(ItemDto request, Long userId, Long itemId);

    ItemDto saveItem(ItemDto request, Long userId);

    List<ItemDto> searchItem(String search, Long userId);

    Item getItemToBooking(Long id);

    CommentResponseDto saveComment(Long itemId, Long userId, CommentResearchDto research);
}
