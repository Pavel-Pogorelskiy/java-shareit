package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestsResponseDto saveItemRequest(long userId, ItemRequestDto request);

    List<ItemRequestsResponseDto> getRequestsToUser(long userId);

    List<ItemRequestsResponseDto> getRequestsToAnotherUsers(long userId, long from, long size);

    ItemRequestsResponseDto getRequest(long userId, long requestId);

}
