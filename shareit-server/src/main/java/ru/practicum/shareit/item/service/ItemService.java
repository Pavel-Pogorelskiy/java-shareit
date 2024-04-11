package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItem(long id, long userId);

    List<ItemDto> getItemToUser(long userId);

    ItemDto updateItem(ItemDto request, long userId, long itemId);

    ItemDto saveItem(ItemDto request, long userId);

    List<ItemDto> searchItem(String search, long userId);

    Item getItemToBooking(long id);

    CommentResponseDto saveComment(long itemId, long userId, CommentResearchDto research);
}
