package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody @Valid ItemDto itemDto) {
        log.info("Сохранение вещи {}", itemDto);
        return itemService.saveItem(itemDto, userId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("Получение вещи itemId = {}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemToUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя с id + " + userId);
        return itemService.getItemToUser(userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Обновление вещи {}", itemDto);
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> getItemToSearch(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam (defaultValue = "") String text) {
        log.info("Поиск вещи по запросу - " + text);
        return itemService.searchItem(text, userId);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentResponseDto saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId,
                                          @Valid @RequestBody CommentResearchDto researchDto) {
        log.info("Сохранение комментария пользователем с id = " + userId + " вещи с id = " + itemId
                + " комментария {}", researchDto);
        return itemService.saveComment(itemId, userId, researchDto);
    }
}
