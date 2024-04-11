package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemBookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.response.BookingRepositoryJpa;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingService bookingService;
    @Mock
    BookingRepositoryJpa bookingRepositoryJpa;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepositoryJpa, userService,
                itemService, new BookingMapperImpl());
    }

    @Test
    void saveBookingTestNotFoundDataExceptionUser() {
        long userId = 1L;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), userId);
        when(userService.getUser(anyLong())).thenThrow(new NotFoundDataException(
                "Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingTestNotFoundDataExceptionItem() {
        long userId = 1L;
        long itemId = 1L;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), userId);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenThrow(new NotFoundDataException(
                "Вещь с id = " + itemId + " не найдена"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));
        assertEquals("Вещь с id = " + itemId + " не найдена", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingTestDateEndBookingBeforeStartException() {
        long userId = 1L;
        long itemId = 1L;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), itemId);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);
        DateEndBookingBeforeStartException ex = assertThrows(
                DateEndBookingBeforeStartException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));
        assertEquals("Дата конца бронирования раньше или равна дате начала", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingTestDateEndBookingStartEqualsEndException() {
        long userId = 1L;
        long itemId = 1L;
        LocalDateTime start = LocalDateTime.now();
        BookingResearchDto bookingResearchDto = new BookingResearchDto(start,
                start, itemId);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);
        DateEndBookingBeforeStartException ex = assertThrows(
                DateEndBookingBeforeStartException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));
        assertEquals("Дата конца бронирования раньше или равна дате начала", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingTestUnavailableItemException() {
        long userId = 1L;
        long itemId = 1L;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", false, user2, null);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);
        UnavailableItemException ex = assertThrows(
                UnavailableItemException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));
        assertEquals("Предмет с id = " + item1.getId() + " недоступен", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingTestUserNotAccessException() {
        long userId = 1L;
        long itemId = 1L;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user1, null);
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);
        UserNotAccessException ex = assertThrows(
                UserNotAccessException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));
        assertEquals("Пользователь с id = " + user1.getId() + " является владельцем вещи " +
                "с id = " + item1.getId(), ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingTest() {
        long userId = 1L;
        long itemId = 1L;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        Booking booking = new Booking(1L, user1, item1, Status.WAITING, bookingResearchDto.getStart(),
                bookingResearchDto.getEnd());
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);
        when(bookingRepositoryJpa.save(any(Booking.class))).thenReturn(booking);
        ItemBookingResponseDto item = new ItemBookingResponseDto(1L, "Предмет 1");
        BookerDto bookerDto = new BookerDto(userId);
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()), item, bookerDto, Status.WAITING);
        BookingResponseDto bookingActual = bookingService.saveBooking(bookingResearchDto, userId);
        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
        verify(bookingRepositoryJpa, atLeast(1)).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingTestThrowsNotFoundDataExceptionUser() {
        long userId = 1L;
        long bookingId = 1L;
        boolean status = true;
        when(userService.getUser(anyLong())).thenThrow(new NotFoundDataException(
                "Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingTestThrowsNotFoundDataExceptionBooking() {
        long userId = 1L;
        long bookingId = 1L;
        boolean status = true;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));
        assertEquals("Запрос на бронирование с id = " + bookingId + " не найден", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingTestThrowsUserNotAccessException() {
        long userId = 1L;
        long bookingId = 1L;
        boolean status = true;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        Booking booking = new Booking(1L, user1, item1, Status.WAITING, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(booking));
        UserNotAccessException ex = assertThrows(
                UserNotAccessException.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));
        assertEquals("Пользователь с id = " + userId + " не является владельцем вещи" +
                " с id = " + booking.getItem().getId(), ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingTestThrowsNotAccessChangeStatus() {
        long userId = 2L;
        long bookingId = 1L;
        boolean status = true;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        Booking booking = new Booking(1L, user1, item1, Status.REJECTED, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        when(userService.getUser(anyLong())).thenReturn(user2);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(booking));
        NotAccessChangeStatus ex = assertThrows(
                NotAccessChangeStatus.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));
        assertEquals("Нет доступа к изменению статуса", ex.getMessage());
        verify(bookingRepositoryJpa, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingTestApproved() {
        long userId = 2L;
        long bookingId = 1L;
        boolean status = true;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1L, "Предмет 1");
        BookerDto bookerDto = new BookerDto(user1.getId());
        Booking booking = new Booking(1L, user1, item1, Status.WAITING, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        Booking bookingApproved = new Booking(1L, user1, item1, Status.APPROVED, booking.getStart(),
                booking.getEnd());
        when(userService.getUser(anyLong())).thenReturn(user2);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepositoryJpa.save(any(Booking.class))).thenReturn(bookingApproved);
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                Status.APPROVED);
        BookingResponseDto bookingActual = bookingService.approvedOrRejectBooking(userId, bookingId, status);
        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
        verify(bookingRepositoryJpa, atLeast(1)).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingTestRejected() {
        long userId = 2L;
        long bookingId = 1L;
        boolean status = false;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1L, "Предмет 1");
        BookerDto bookerDto = new BookerDto(user1.getId());
        Booking booking = new Booking(1L, user1, item1, Status.WAITING, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        Booking bookingRejected = new Booking(1L, user1, item1, Status.REJECTED, booking.getStart(),
                booking.getEnd());
        when(userService.getUser(anyLong())).thenReturn(user2);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepositoryJpa.save(any(Booking.class))).thenReturn(bookingRejected);
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                Status.REJECTED);
        BookingResponseDto bookingActual = bookingService.approvedOrRejectBooking(userId, bookingId, status);
        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
        verify(bookingRepositoryJpa, atLeast(1)).save(any(Booking.class));
    }

    @Test
    void getBookingTestThrowsNotFoundDataExceptionUser() {
        long userId = 1L;
        long bookingId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenThrow(new NotFoundDataException(
                "Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.getBooking(userId, bookingId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getBookingTestThrowsNotFoundDataExceptionBooking() {
        long userId = 1L;
        long bookingId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.getBooking(userId, bookingId));
        assertEquals("Запрос на бронирование с id = " + bookingId + " не найден", ex.getMessage());
    }

    @Test
    void getBookingTestThrowsUserNotAccessException() {
        long userId = 3L;
        long bookingId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(3L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        Booking booking = new Booking(1L, user1, item1, Status.APPROVED, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        when(userService.getUser(anyLong())).thenReturn(user3);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(booking));
        UserNotAccessException ex = assertThrows(
                UserNotAccessException.class,
                () -> bookingService.getBooking(userId, bookingId));
        assertEquals("Пользователь с id = " + userId + " не является владельцем вещи" +
                " с id = " + item1.getId() + " или владельцем запроса на бронирования id = "
                + bookingId, ex.getMessage());
    }

    @Test
    void getBookingTest() {
        long userId = 1L;
        long bookingId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        Booking booking = new Booking(1L, user1, item1, Status.APPROVED, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1L, "Предмет 1");
        BookerDto bookerDto = new BookerDto(user1.getId());
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                Status.APPROVED);
        BookingResponseDto bookingActual = bookingService.getBooking(userId, bookingId);
        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
    }

    @Test
    void getBookingTestOwner() {
        long userId = 2L;
        long bookingId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание 1", true, user2, null);
        Booking booking = new Booking(1L, user1, item1, Status.APPROVED, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1L, "Предмет 1");
        BookerDto bookerDto = new BookerDto(user1.getId());
        when(userService.getUser(anyLong())).thenReturn(user2);
        when(bookingRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                Status.APPROVED);
        BookingResponseDto bookingActual = bookingService.getBooking(userId, bookingId);
        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
    }

    @Test
    void getBookingToUserTestThrowsNotFoundDataException() {
        long userId = 1L;
        String state = "";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenThrow(new NotFoundDataException(
                "Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.getBookingToUser(userId, state, from, size));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getBookingToUserTestThrowsNotFoundStateException() {
        long userId = 1L;
        String state = "STATE";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        NotFoundStateException ex = assertThrows(
                NotFoundStateException.class,
                () -> bookingService.getBookingToUser(userId, state, from, size));
        assertEquals("Unknown state: " + state, ex.getMessage());
    }

    @Test
    void getBookingToUserTestStateALL() {
        long userId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByBooker_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByBooker_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserTestStateCURRENT() {
        long userId = 1L;
        String state = "CURRENT";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByBooker_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByBooker_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserTestStatePAST() {
        long userId = 1L;
        String state = "PAST";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByBooker_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByBooker_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserTestStateFUTURE() {
        long userId = 1L;
        String state = "FUTURE";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByBooker_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByBooker_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserTestStateWAITING() {
        long userId = 1L;
        String state = "WAITING";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByBooker_IdAndStatus(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByBooker_IdAndStatus(anyLong(), any(Status.class));
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserTestStateREJECTED() {
        long userId = 1L;
        String state = "REJECTED";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByBooker_IdAndStatus(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByBooker_IdAndStatus(anyLong(), any(Status.class));
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerTestThrowsNotFoundDataException() {
        long userId = 1L;
        String state = "";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenThrow(new NotFoundDataException(
                "Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> bookingService.getBookingToOwner(userId, state, from, size));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getBookingToOwnerTestThrowsNotFoundStateException() {
        long userId = 1L;
        String state = "STATE";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        NotFoundStateException ex = assertThrows(
                NotFoundStateException.class,
                () -> bookingService.getBookingToOwner(userId, state, from, size));
        assertEquals("Unknown state: " + state, ex.getMessage());
    }

    @Test
    void getBookingToOwnerTestStateALL() {
        long userId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByItem_Owner_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByItem_Owner_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerTestStateCURRENT() {
        long userId = 1L;
        String state = "CURRENT";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByItem_Owner_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByItem_Owner_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerTestStatePAST() {
        long userId = 1L;
        String state = "PAST";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByItem_Owner_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByItem_Owner_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerTestStateFUTURE() {
        long userId = 1L;
        String state = "FUTURE";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByItem_Owner_IdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByItem_Owner_IdOrderByStartDesc(anyLong());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerTestStateWAITING() {
        long userId = 1L;
        String state = "WAITING";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByItem_Owner_IdAndStatus(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByItem_Owner_IdAndStatus(anyLong(), any(Status.class));
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerTestStateREJECTED() {
        long userId = 1L;
        String state = "REJECTED";
        long from = 0;
        long size = 20;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user1);
        when(bookingRepositoryJpa.findBookingByItem_Owner_IdAndStatus(anyLong(), any(Status.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);
        verify(bookingRepositoryJpa, atLeast(1))
                .findBookingByItem_Owner_IdAndStatus(anyLong(), any(Status.class));
        assertEquals(0, bookingsResponse.size());
    }
}