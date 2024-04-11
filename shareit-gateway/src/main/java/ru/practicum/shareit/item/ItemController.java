package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody @Valid ItemDto itemDto) {
        log.info("Сохранение вещи {}", itemDto);
        return itemClient.saveItem(itemDto, userId);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("Получение вещи {}", itemClient.getItem(itemId, userId));
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemToUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя с id + " + userId + ": {}", itemClient.getItemToUser(userId));
        return itemClient.getItemToUser(userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Обновление вещи {}", itemDto);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> getItemToSearch(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam (defaultValue = "") String text) {
        log.info("Поиск вещи по запросу - " + text + ": {}", itemClient.searchItem(text, userId));
        return itemClient.searchItem(text, userId);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @Valid @RequestBody CommentResearchDto researchDto) {
        log.info("Сохранение комментария пользователем с id = " + userId + " вещи с id = " + itemId
                + " комментария {}", researchDto);
        return itemClient.saveComment(itemId, userId, researchDto);
    }
}
