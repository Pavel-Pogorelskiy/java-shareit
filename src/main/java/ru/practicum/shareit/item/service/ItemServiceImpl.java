package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.response.ItemResponse;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemResponse itemResponse;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private Long idItem = 0L;

    @Override
    public ItemDto getItem(Long id) {
        return itemMapper.toDto(itemResponse.get(id));
    }

    @Override
    public List<ItemDto> getItemToUser(Long userId) {
        return itemMapper.toDtoList(itemResponse.getAll(userId));
    }

    @Override
    public ItemDto updateItem(ItemDto request, Long userId, Long itemId) {
        validationUser(userId);
        return itemMapper.toDto(itemResponse.update(itemId, itemMapper.toItem(request), userId));
    }

    @Override
    public ItemDto saveItem(ItemDto request, Long userId) {
        validationUser(userId);
        Item item = itemMapper.toItem(request);
        item.setId(idItem());
        item.setOwner(createUser(userId));
        return itemMapper.toDto(itemResponse.save(item));
    }

    private Long idItem() {
        return ++idItem;
    }

    private User createUser(Long idUser) {
        return userService.getUser(idUser);
    }

    @Override
    public void validationUser(Long id) {
        if (userService.getUser(id) == null) {
            throw new NotFoundDataException("Пользователя с id = " + id + " не существует");
        }
    }

    @Override
    public List<ItemDto> searchItem(String search, Long userId) {
        validationUser(userId);
        return itemMapper.toDtoList(itemResponse.searchItem(search));
    }
}
