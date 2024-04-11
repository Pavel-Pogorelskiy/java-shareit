package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestsResponseDto saveItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Сохранение запроса {}", requestDto);
        return itemRequestService.saveItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestsResponseDto> getRequestsToUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех запросов пользователя с id = {}: {}", userId,
                itemRequestService.getRequestsToUser(userId));
        return itemRequestService.getRequestsToUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestsResponseDto> getRequestsToAnotherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
             @RequestParam(defaultValue = "0") @Min(0) Long from,
             @RequestParam(required = false) @Min(1) Long size) {
        if (size == null) {
            log.info("Получение всех запросов других пользователь пользователем с id = {}: {}", userId,
                    itemRequestService.getRequestsToAnotherUsers(userId, from, Long.MAX_VALUE));
            return itemRequestService.getRequestsToAnotherUsers(userId, from, Long.MAX_VALUE);
        } else {
            log.info("Получение всех запросов других пользователь пользователем с id = {}: {}", userId,
                    itemRequestService.getRequestsToAnotherUsers(userId, from, size));
            return itemRequestService.getRequestsToAnotherUsers(userId, from, size);
        }
    }

    @GetMapping("/{requestId}")
    public ItemRequestsResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long requestId) {
        log.info("Получение запроса пользователем с id = {}: {}", userId,
                itemRequestService.getRequest(userId, requestId));
        return itemRequestService.getRequest(userId, requestId);
    }
}
