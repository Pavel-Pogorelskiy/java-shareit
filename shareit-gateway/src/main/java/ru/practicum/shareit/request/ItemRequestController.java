package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Сохранение запроса {}", requestDto);
        return itemRequestClient.saveItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsToUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех запросов пользователя с id = {}: {}", userId,
                itemRequestClient.getRequestsToUser(userId));
        return itemRequestClient.getRequestsToUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsToAnotherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
             @RequestParam(defaultValue = "0") @Min(0) Long from,
             @RequestParam(required = false) @Min(1) Long size) {
        if (size == null) {
            log.info("Получение всех запросов других пользователь пользователем с id = {}: {}", userId,
                    itemRequestClient.getRequestsToAnotherUsers(userId, from, Long.MAX_VALUE));
            return itemRequestClient.getRequestsToAnotherUsers(userId, from, Long.MAX_VALUE);
        } else {
            log.info("Получение всех запросов других пользователь пользователем с id = {}: {}", userId,
                    itemRequestClient.getRequestsToAnotherUsers(userId, from, size));
            return itemRequestClient.getRequestsToAnotherUsers(userId, from, size);
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Получение запроса пользователем с id = {}: {}", userId,
                itemRequestClient.getRequest(userId, requestId));
        return itemRequestClient.getRequest(userId, requestId);
    }
}
