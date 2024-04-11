package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.ItemToRequestResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.response.ItemRepositoryJpa;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.response.ItemRequestJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestJpa itemRequestJpa;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestsResponseDto saveItemRequest(long userId, ItemRequestDto request) {
        User user = userService.getUser(userId);
        ItemRequest itemRequest = itemRequestMapper.toRequest(request);
        itemRequest.setOwner(user);
        return itemRequestMapper.toDto(itemRequestJpa.save(itemRequest));
    }

    @Override
    public List<ItemRequestsResponseDto> getRequestsToUser(long userId) {
        User user = userService.getUser(userId);
        List<ItemRequestsResponseDto> requests = itemRequestMapper
                .toDtoList(itemRequestJpa.findItemRequestByOwner_IdOrderByCreatedDesc(user.getId()));
        return getItemRequestsResponseDto(requests);
    }

    private List<ItemRequestsResponseDto> getItemRequestsResponseDto(List<ItemRequestsResponseDto> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequestsResponseDto::getId)
                .collect(toList());
        Map<Long, List<ItemToRequestResponse>> itemsByRequest = itemRepositoryJpa.findByRequest_IdIn(requestIds)
                .stream()
                .map(itemMapper::toDtoItemToRequest)
                .collect(groupingBy(ItemToRequestResponse::getRequestId, toList()));
        if (!itemsByRequest.isEmpty()) {
            requests.stream()
                    .peek(r -> r.setItems(itemsByRequest.getOrDefault(r.getId(), Collections.emptyList())))
                    .collect(toList());
        }
        return requests;
    }

    @Override
    public List<ItemRequestsResponseDto> getRequestsToAnotherUsers(long userId, long from, long size) {
        User user = userService.getUser(userId);
        List<ItemRequestsResponseDto> requests = itemRequestMapper.toDtoList(itemRequestJpa
                        .findItemRequestNotByOwner_IdOrderByCreatedDesc(user.getId())).stream()
                .skip(from)
                .limit(size)
                .collect(toList());
        return getItemRequestsResponseDto(requests);
    }

    @Override
    public ItemRequestsResponseDto getRequest(long userId, long requestId) {
        userService.getUser(userId);
        if (itemRequestJpa.findById(requestId).isEmpty()) {
            throw new NotFoundDataException("Запроса с id = " + requestId + " не существует");
        }
        ItemRequestsResponseDto request = itemRequestMapper.toDto(itemRequestJpa.findById(requestId).get());
        request.setItems(itemMapper.toDtoListItemsRequest(itemRepositoryJpa.findByRequest_Id(requestId)));
        return request;
    }
}
