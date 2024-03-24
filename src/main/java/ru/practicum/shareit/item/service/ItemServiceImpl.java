package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseToItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.response.BookingRepositoryJpa;
import ru.practicum.shareit.exception.AnotherUserException;
import ru.practicum.shareit.exception.NotBookingException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.response.CommentRepositoryJpa;
import ru.practicum.shareit.item.response.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryJpa itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingRepositoryJpa bookingRepositoryJpa;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final CommentRepositoryJpa commentRepositoryJpa;

    @Override
    public ItemDto getItem(long id, long userId) {
        User user = userService.getUser(userId);
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new NotFoundDataException("Вещь с id = " + id + " не найдена");
        }
        ItemDto itemResponse = itemMapper.toDto(item.get());
        BookingResponseToItemDto nextBooking = null;
        BookingResponseToItemDto lastBooking = null;
        if (item.get().getOwner().getId().longValue() == user.getId()) {
            if (!bookingRepositoryJpa.findBookingByItemAndStartAfter(id, Status.APPROVED).isEmpty()) {
                nextBooking = bookingMapper.toBookingToItem(bookingRepositoryJpa
                        .findBookingByItemAndStartAfter(id, Status.APPROVED).get(0));
            }
            if (!bookingRepositoryJpa.findBookingByItemAndStartBefore(id, Status.APPROVED).isEmpty()) {
                lastBooking = bookingMapper.toBookingToItem(bookingRepositoryJpa
                        .findBookingByItemAndStartBefore(id, Status.APPROVED).get(0));
            }
        }
        itemResponse.setNextBooking(nextBooking);
        itemResponse.setLastBooking(lastBooking);
        if (!commentRepositoryJpa.findCommentByItem_Id(id).isEmpty()) {
            itemResponse.setComments(commentMapper.toListCommentDto(commentRepositoryJpa
                    .findCommentByItem_Id(id)));
        }
        return itemResponse;
    }

    @Override
    public List<ItemDto> getItemToUser(long userId) {
        userService.getUser(userId);
        return itemMapper.toDtoList(itemRepository
                        .findByOwnerIdOrderByIdAsc(userId)).stream()
                .peek(
                        i -> {
                            if (!bookingRepositoryJpa.findBookingByItemAndStartAfter(i.getId(), Status.APPROVED)
                                    .isEmpty()) {
                                i.setNextBooking(bookingMapper.toBookingToItem(bookingRepositoryJpa
                                        .findBookingByItemAndStartAfter(i.getId(), Status.APPROVED).get(0)));
                            }
                            if (!bookingRepositoryJpa.findBookingByItemAndStartBefore(i.getId(), Status.APPROVED)
                                    .isEmpty()) {
                                i.setLastBooking(bookingMapper.toBookingToItem(bookingRepositoryJpa
                                        .findBookingByItemAndStartBefore(i.getId(), Status.APPROVED).get(0)));
                            }
                            if (commentRepositoryJpa.findCommentByItem_Owner_Id(userId).isEmpty()) {
                            i.setComments(commentMapper.toListCommentDto(commentRepositoryJpa
                                    .findCommentByItem_Owner_Id(userId)));
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(ItemDto request, long userId, long itemId) {
        Item item = itemRepository.findById(itemId).get();
        if (item.getOwner().getId() != userId) {
            throw new AnotherUserException("Пользователь с id = " + userId + " не имеет права " +
                    "на изменение вещи, так как не является ее владельцем");
        }
        Item itemRequest = itemMapper.toItem(request);
        if (itemRequest.getName() == null) {
            itemRequest.setName(item.getName());
        }
        if (itemRequest.getDescription() == null) {
            itemRequest.setDescription(item.getDescription());
        }
        if (itemRequest.getAvailable() == null) {
            itemRequest.setAvailable(item.getAvailable());
        }
        itemRequest.setId(itemId);
        itemRequest.setOwner(item.getOwner());
        return itemMapper.toDto(itemRepository.save(itemRequest));
    }

    @Override
    public ItemDto saveItem(ItemDto request, long userId) {
        userService.getUser(userId);
        Item item = itemMapper.toItem(request);
        item.setOwner(createUser(userId));
        return itemMapper.toDto(itemRepository.save(item));
    }

    private User createUser(long idUser) {
        return userService.getUser(idUser);
    }

    @Override
    public List<ItemDto> searchItem(String search, long userId) {
        userService.getUser(userId);
        List<ItemDto> items = new ArrayList<>();
        if (!search.isBlank()) {
            items = itemMapper.toDtoList(itemRepository.findByName(search, search));
        }
        return items;
    }

    @Override
    public Item getItemToBooking(long id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new NotFoundDataException("Вещь с id = " + id + " не найдена");
        }
        return item.get();
    }

    @Override
    public CommentResponseDto saveComment(long itemId, long userId, CommentResearchDto research) {
        User user = userService.getUser(userId);
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundDataException("Вещь с id = " + itemId + " не найдена");
        }
        if (bookingRepositoryJpa.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(itemId, userId,
                research.getCreated(), Status.APPROVED).isEmpty()) {
            throw new NotBookingException("Завершенных бронирований для пользователя с id = " +
                    userId + " по предмету с id = " + itemId + "не существует");
        }
        Comment comment = commentMapper.toComment(research);
        comment.setAuthor(user);
        comment.setItem(item.get());
        return commentMapper.toDto(commentRepositoryJpa.save(comment));
    }
}
