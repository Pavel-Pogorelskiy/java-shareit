package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemMapper {
    Item toItem(ItemDto itemDto);

    ItemDto toDto(Item item);

    List<ItemDto> toDtoList(List<Item> items);

}
