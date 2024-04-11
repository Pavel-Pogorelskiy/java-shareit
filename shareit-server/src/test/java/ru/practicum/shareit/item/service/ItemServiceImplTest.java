package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponseToItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.response.BookingRepositoryJpa;
import ru.practicum.shareit.exception.AnotherUserException;
import ru.practicum.shareit.exception.NotBookingException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.response.CommentRepositoryJpa;
import ru.practicum.shareit.item.response.ItemRepositoryJpa;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.response.ItemRequestJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private ItemService itemService;
    @Mock
    private ItemRepositoryJpa itemRepositoryJpa;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepositoryJpa bookingRepositoryJpa;
    @Mock
    private ItemRequestJpa itemRequestJpa;
    @Mock
    private CommentRepositoryJpa commentRepositoryJpa;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepositoryJpa, new ItemMapperImpl(), userService, bookingRepositoryJpa,
                itemRequestJpa, new BookingMapperImpl(), new CommentMapperImpl(), commentRepositoryJpa);
    }

    @Test
    void getItemTestNotFoundDataException() {
        long itemId = 1L;
        long userId = 1L;
        when(userService.getUser(anyLong())).thenThrow(
                new NotFoundDataException("Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.getItem(itemId, userId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getItemTestUserNotFoundDataException() {
        long itemId = 1L;
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.getItem(itemId, userId));
        assertEquals("Вещь с id = " + itemId + " не найдена", ex.getMessage());
    }

    @Test
    void getItemTestNotOwnerNotComment() {
        long itemId = 1L;
        long userId = 2L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        List<Comment> comments = new ArrayList<>();
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        when(commentRepositoryJpa.findCommentByItem_Id(anyLong())).thenReturn(comments);
        ItemDto itemExpected = new ItemDto(1L, "Предмет 1", "Описание 1",
                true, null, null, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId,userId);
        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemTestNotOwner() {
        long itemId = 1L;
        long userId = 2L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(1L, "Комментарий 1", user1, item, created);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        List<CommentResponseDto> commentsResponseDto = new ArrayList<>();
        CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "Комментарий 1",
                "Пользователь 1", created);
        commentsResponseDto.add(commentResponseDto);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        when(commentRepositoryJpa.findCommentByItem_Id(anyLong())).thenReturn(comments);
        ItemDto itemExpected = new ItemDto(1L, "Предмет 1", "Описание 1",
                true, null, null, commentsResponseDto, null);
        ItemDto itemActual = itemService.getItem(itemId,userId);
        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemTestNotCommentsNotLastBookingAndNotNextBooking() {
        long itemId = 1L;
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        when(bookingRepositoryJpa.findBookingByItemAndStartAfter(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepositoryJpa.findBookingByItemAndStartBefore(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        ItemDto itemExpected = new ItemDto(1L, "Предмет 1", "Описание 1",
                true, null, null, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId,userId);
        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemTestNotCommentsNotNextBooking() {
        long itemId = 1L;
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now().minusDays(15);
        Booking bookingLast = new Booking(1L, user1, item, Status.APPROVED, start, end);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingLast);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        when(bookingRepositoryJpa.findBookingByItemAndStartAfter(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepositoryJpa.findBookingByItemAndStartBefore(anyLong(), any(Status.class)))
                .thenReturn(bookings);
        BookingResponseToItemDto bookingLastResponse = new BookingResponseToItemDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(end),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(start), 1L);
        ItemDto itemExpected = new ItemDto(1L, "Предмет 1", "Описание 1",
                true, null, bookingLastResponse, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId,userId);
        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemTestNotCommentsNotLastBooking() {
        long itemId = 1L;
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime startNext = LocalDateTime.now().plusDays(30);
        LocalDateTime endNext = LocalDateTime.now().plusDays(90);
        Booking bookingNext = new Booking(1L, user1, item, Status.APPROVED, startNext, endNext);
        List<Booking> bookingsNext = new ArrayList<>();
        bookingsNext.add(bookingNext);
        LocalDateTime startEnd = LocalDateTime.now().minusDays(30);
        LocalDateTime endEnd = LocalDateTime.now().minusDays(15);
        Booking bookingLast = new Booking(1L, user1, item, Status.APPROVED, startEnd, endEnd);
        List<Booking> bookingsLast = new ArrayList<>();
        bookingsLast.add(bookingLast);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        when(bookingRepositoryJpa.findBookingByItemAndStartAfter(anyLong(), any(Status.class)))
                .thenReturn(bookingsNext);
        when(bookingRepositoryJpa.findBookingByItemAndStartBefore(anyLong(), any(Status.class)))
                .thenReturn(bookingsLast);
        BookingResponseToItemDto bookingNextResponse = new BookingResponseToItemDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endNext),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startNext), 1L);
        BookingResponseToItemDto bookingLastResponse = new BookingResponseToItemDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endEnd),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startEnd), 1L);
        ItemDto itemExpected = new ItemDto(1L, "Предмет 1", "Описание 1",
                true, bookingNextResponse, bookingLastResponse, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId,userId);
        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemTest() {
        long itemId = 1L;
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime start = LocalDateTime.now().plusDays(30);
        LocalDateTime end = LocalDateTime.now().plusDays(90);
        Booking bookingNext = new Booking(1L, user1, item, Status.APPROVED, start, end);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNext);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(1L, "Комментарий 1", user1, item, created);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        List<CommentResponseDto> commentsResponseDto = new ArrayList<>();
        CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "Комментарий 1",
                "Пользователь 1", created);
        commentsResponseDto.add(commentResponseDto);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        when(bookingRepositoryJpa.findBookingByItemAndStartAfter(anyLong(), any(Status.class)))
                .thenReturn(bookings);
        when(bookingRepositoryJpa.findBookingByItemAndStartBefore(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        when(commentRepositoryJpa.findCommentByItem_Id(anyLong())).thenReturn(comments);
        BookingResponseToItemDto bookingNextResponse = new BookingResponseToItemDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(end),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(start), 1L);
        ItemDto itemExpected = new ItemDto(1L, "Предмет 1", "Описание 1",
                true, bookingNextResponse, null, commentsResponseDto, null);
        ItemDto itemActual = itemService.getItem(itemId,userId);
        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemToUserTestThrowsNotFoundDataException() {
        long userId = 1L;
        when(userService.getUser(anyLong())).thenThrow(
                new NotFoundDataException("Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.getItemToUser(anyLong()));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getItemToUserTest() {
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findByOwnerIdOrderByIdAsc(anyLong())).thenReturn(new ArrayList<>());
        List<ItemDto> itemsActual = itemService.getItemToUser(userId);
        assertEquals(0, itemsActual.size());
    }

    @Test
    void updateItemTestNotFoundDataException() {
        ItemDto itemUpdateDto = new ItemDto(null, "Предмет обновленный 1", null,
                null, null, null, null, null);
        long userId = 1L;
        long itemId = 1L;
        when(itemRepositoryJpa.findById(anyLong())).thenThrow(new NotFoundDataException(
                "Вещь с id = " + itemId + " не найдена"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.updateItem(itemUpdateDto, userId, itemId));
        assertEquals("Вещь с id = " + itemId + " не найдена", ex.getMessage());
        verify(itemRepositoryJpa, never()).save(any(Item.class));
    }

    @Test
    void updateItemTestAnotherUserException() {
        ItemDto itemUpdateDto = new ItemDto(null, "Предмет обновленный 1", null,
                null, null, null, null, null);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        long userId = 2L;
        long itemId = 1L;
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(item);
        AnotherUserException ex = assertThrows(
                AnotherUserException.class,
                () -> itemService.updateItem(itemUpdateDto, userId, itemId));
        assertEquals("Пользователь с id = " + userId + " не имеет права " +
                "на изменение вещи, так как не является ее владельцем", ex.getMessage());
        verify(itemRepositoryJpa, never()).save(any(Item.class));
    }

    @Test
    void updateItemTestName() {
        ItemDto itemUpdateDto = new ItemDto(null, "Предмет обновленный 1", null,
                null, null, null, null, null);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemExpectedDto = new ItemDto(1L, "Предмет обновленный 1", "Описание 1",
                true, null, null, new ArrayList<>(), null);
        Item itemUpdate = new Item(1L, "Предмет обновленный 1", "Описание 1",
                true, user1, null);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(item);
        when(itemRepositoryJpa.save(any(Item.class))).thenReturn(itemUpdate);
        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepositoryJpa, atLeast(1)).save(any(Item.class));
    }

    @Test
    void updateItemTestDescription() {
        ItemDto itemUpdateDto = new ItemDto(null, null, "Описание новое 1",
                null, null, null, null, null);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemExpectedDto = new ItemDto(1L, "Предмет 1", "Описание новое 1",
                true, null, null, new ArrayList<>(), null);
        Item itemUpdate = new Item(1L, "Предмет 1", "Описание новое 1",
                true, user1, null);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(item);
        when(itemRepositoryJpa.save(any(Item.class))).thenReturn(itemUpdate);
        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepositoryJpa, atLeast(1)).save(any(Item.class));
    }

    @Test
    void updateItemTestAvailable() {
        ItemDto itemUpdateDto = new ItemDto(null, null, null,
                false, null, null, null, null);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemExpectedDto = new ItemDto(1L, "Предмет 1", "Описание 1",
                false, null, null, new ArrayList<>(), null);
        Item itemUpdate = new Item(1L, "Предмет 1", "Описание 1",
                false, user1, null);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(item);
        when(itemRepositoryJpa.save(any(Item.class))).thenReturn(itemUpdate);
        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepositoryJpa, atLeast(1)).save(any(Item.class));
    }

    @Test
    void updateItemTestAll() {
        ItemDto itemUpdateDto = new ItemDto(null, "Предмет обновленный 1", "Описание новое 1",
                false, null, null, null, null);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemExpectedDto = new ItemDto(1L, "Предмет обновленный 1", "Описание новое 1",
                false, null, null, new ArrayList<>(), null);
        Item itemUpdate = new Item(1L, "Предмет обновленный 1", "Описание новое 1",
                false, user1, null);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(item);
        when(itemRepositoryJpa.save(any(Item.class))).thenReturn(itemUpdate);
        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepositoryJpa, atLeast(1)).save(any(Item.class));
    }

    @Test
    void saveItemTestThrowsNotFoundDataException() {
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        long id = 1L;
        when(userService.getUser(anyLong())).thenThrow(
                new NotFoundDataException("Пользователя с id = " + id + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.saveItem(itemDto1, id));
        assertEquals("Пользователя с id = " + id + " не найден", ex.getMessage());
        verify(itemRepositoryJpa, never()).save(any(Item.class));
    }

    @Test
    void saveItemTestRequestIdNull() {
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Item saveItem = new Item(1L, "Предмет 1", "Описание 1", true,
                user1, null);
        ItemDto itemDtoExpected = new ItemDto(1L, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.save(any(Item.class))).thenReturn(saveItem);
        assertEquals(itemDtoExpected, itemService.saveItem(itemDto1, 1L));
        verify(itemRepositoryJpa, atLeast(1)).save(any(Item.class));
    }

    @Test
    void saveItemTestRequestIdNotNull() {
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Item saveItem = new Item(1L, "Предмет 1", "Описание 1", true,
                user1, null);
        ItemDto itemDtoExpected = new ItemDto(1L, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.save(any(Item.class))).thenReturn(saveItem);
        assertEquals(itemDtoExpected, itemService.saveItem(itemDto1, 1L));
        verify(itemRepositoryJpa, atLeast(1)).save(any(Item.class));
    }

    @Test
    void saveItemTestRequestIdOneThrowNotFoundDataException() {
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, 1L);
        when(itemRequestJpa.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.saveItem(itemDto1, 1L));
        assertEquals("Запрос с id = " + itemDto1.getRequestId() + "не найден", ex.getMessage());
        verify(itemRepositoryJpa, never()).save(any(Item.class));
    }

    @Test
    void saveItemTestRequestIdOne() {
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, 1L);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        long id = 1L;
        ItemRequest request = new ItemRequest(1L, "Не важно", LocalDateTime.now(), user2);
        Item saveItem = new Item(1L, "Предмет 1", "Описание 1", true,
                user1, request);
        when(itemRequestJpa.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepositoryJpa.save(any(Item.class))).thenReturn(saveItem);
        ItemDto itemDtoExpected = new ItemDto(1L, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), 1L);
        assertEquals(itemDtoExpected, itemService.saveItem(itemDto1, id));
        verify(itemRepositoryJpa, atLeast(1)).save(any(Item.class));
    }

    @Test
    void searchItemTestThrowsNotFoundDataException() {
        String search = "дрель";
        long userId = 1L;
        when(userService.getUser(anyLong())).thenThrow(
                new NotFoundDataException("Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.searchItem(search, userId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void searchItemTestSearchIsBlank() {
        String search = "";
        long userId = 1L;
        when(userService.getUser(anyLong())).thenReturn(
                new User(1L, "Пользователь 1", "user1@yandex.ru"));
        assertEquals(0, itemService.searchItem(search, userId).size());
    }

    @Test
    void searchItemTestSearchOneItem() {
        String search = "Предмет 1";
        long userId = 1L;
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "Предмет 1", "Описание 1", true,
                new User(2L, "Пользователь 2", "user2@yandex.ru"), null));
        when(userService.getUser(anyLong())).thenReturn(
                new User(1L, "Пользователь 1", "user1@yandex.ru"));
        when(itemRepositoryJpa.findByNameAndDescription(anyString(),anyString())).thenReturn(items);
        assertEquals(1, itemService.searchItem(search, userId).size());
    }

    @Test
    void getItemToBookingTestThrowsNotFoundDataException() {
        long id = 1L;
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.getItemToBooking(id));
        assertEquals("Вещь с id = " + id + " не найдена", ex.getMessage());
    }

    @Test
    void getItemToBookingTest() {
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user1, null));
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        Item itemActual = itemService.getItemToBooking(1L);
        Item itemExpected = itemOptional.get();
        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getOwner(), itemActual.getOwner());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getRequest(), itemActual.getRequest());
    }

    @Test
    void saveCommentTestNotFoundDataException() {
        long itemId = 1L;
        long userId = 1L;
        CommentResearchDto comment = new CommentResearchDto("Комментарий 1", LocalDateTime.now());
        when(userService.getUser(anyLong())).thenThrow(
                new NotFoundDataException("Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.saveComment(itemId, userId, comment));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void saveCommentTestItemNotFoundDataException() {
        long itemId = 1L;
        long userId = 1L;
        CommentResearchDto comment = new CommentResearchDto("Комментарий 1", LocalDateTime.now());
        User user = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.saveComment(itemId, userId, comment));
        assertEquals("Вещь с id = " + itemId + " не найдена", ex.getMessage());
    }

    @Test
    void saveCommentTestNotBookingException() {
        long itemId = 1L;
        long userId = 1L;
        CommentResearchDto comment = new CommentResearchDto("Комментарий 1", LocalDateTime.now());
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user2, null));
        List<Booking> bookings = new ArrayList<>();
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(item);
        when(bookingRepositoryJpa.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(
                anyLong(), anyLong(), any(LocalDateTime.class), any(Status.class))).thenReturn(bookings);
        NotBookingException ex = assertThrows(
                NotBookingException.class,
                () -> itemService.saveComment(itemId, userId, comment));
        assertEquals("Завершенных бронирований для пользователя с id = " +
                userId + " по предмету с id = " + itemId + "не существует", ex.getMessage());
    }

    @Test
    void saveCommentTest() {
        long itemId = 1L;
        long userId = 1L;
        CommentResearchDto commentDto = new CommentResearchDto("Комментарий 1", LocalDateTime.now());
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        LocalDateTime created = LocalDateTime.now();
        Optional<Item> itemOptional = Optional.of(new Item(1L, "Предмет 1", "Описание 1",
                true, user2, null));
        Item item = new Item(1L, "Предмет 1", "Описание 1",
                true, user2, null);
        Comment commentSave = new Comment(1L, "Комментарий 1", user1, item, created);
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking(1L, user1, item, Status.APPROVED, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(10));
        CommentResponseDto commentExpected = new CommentResponseDto(1L, "Комментарий 1",
                "Пользователь 1", created);
        bookings.add(booking1);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemRepositoryJpa.findById(anyLong())).thenReturn(itemOptional);
        when(bookingRepositoryJpa.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(
                anyLong(), anyLong(), any(LocalDateTime.class), any(Status.class))).thenReturn(bookings);
        when(commentRepositoryJpa.save(any(Comment.class))).thenReturn(commentSave);
        CommentResponseDto commentActual = itemService.saveComment(itemId, userId, commentDto);
        assertEquals(commentExpected.getId(), commentActual.getId());
        assertEquals(commentExpected.getText(), commentActual.getText());
        assertEquals(commentExpected.getCreated(), commentActual.getCreated());
        assertEquals(commentExpected.getAuthorName(), commentActual.getAuthorName());
        verify(commentRepositoryJpa, atLeast(1)).save(any(Comment.class));
    }
}