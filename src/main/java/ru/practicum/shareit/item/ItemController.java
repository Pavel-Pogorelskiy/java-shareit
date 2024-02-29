package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.info("Получение вещи {}", itemService.getItem(itemId));
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemToUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя с id + " + userId + ": {}", itemService.getItemToUser(userId));
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
        log.info("Поиск вещи по запросу - " + text + ": {}", itemService.searchItem(text, userId));
        return itemService.searchItem(text, userId);
    }
}
