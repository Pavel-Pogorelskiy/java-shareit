package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemBookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.UserNotAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    void saveBookingTestThrowsUserNotAccessException() {
        long itemId = 1L;
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user2.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        UserNotAccessException ex = assertThrows(
                UserNotAccessException.class,
                () -> bookingService.saveBooking(bookingResearchDto, user2.getId()));
        assertEquals("Пользователь с id = " + user2.getId() + " является владельцем вещи " +
                "с id = " + itemId, ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveBookingTest() {
        long itemId = 1L;
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        BookingResponseDto bookingActual = bookingService.saveBooking(bookingResearchDto, user2.getId());
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()),
                new ItemBookingResponseDto(1L, "Предмет 1"), new BookerDto(user2.getId()), Status.WAITING);
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
    @DirtiesContext
    void approvedOrRejectBookingTestThrowsUserNotAccessException() {
        long itemId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());
        UserNotAccessException ex = assertThrows(
                UserNotAccessException.class,
                () -> bookingService.approvedOrRejectBooking(user2.getId(), bookingId, approved));
        assertEquals("Пользователь с id = " + user2.getId() + " не является владельцем вещи" +
                " с id = " + itemId, ex.getMessage());
    }

    @Test
    @DirtiesContext
    void approvedOrRejectBookingTestREJECTED() {
        long itemId = 1L;
        long bookingId = 1L;
        boolean approved = false;
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());
        BookingResponseDto bookingActual = bookingService.approvedOrRejectBooking(user1.getId(), bookingId, approved);
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()),
                new ItemBookingResponseDto(1L, "Предмет 1"), new BookerDto(user2.getId()), Status.REJECTED);
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
    @DirtiesContext
    void getBookingTestThrowsUserNotAccessException() {
        long itemId = 1L;
        long bookingId = 1L;
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        User user3 = userService.saveUser(new UserDto("Пользователь 3", "user3@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());
        UserNotAccessException ex = assertThrows(
                UserNotAccessException.class,
                () -> bookingService.getBooking(user3.getId(), bookingId));
        assertEquals("Пользователь с id = " + user3.getId() + " не является владельцем вещи" +
                " с id = " + itemId + " или владельцем запроса на бронирования id = "
                + bookingId, ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getBookingTest() {
        long itemId = 1L;
        long bookingId = 1L;
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());
        bookingService.approvedOrRejectBooking(user1.getId(), bookingId, true);
        BookingResponseDto bookingActual = bookingService.getBooking(user2.getId(), bookingId);
        BookingResponseDto bookingExpected = new BookingResponseDto(1L,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()),
                new ItemBookingResponseDto(1L, "Предмет 1"), new BookerDto(user2.getId()), Status.APPROVED);
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
    @DirtiesContext
    void getBookingToUser() {
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        User user3 = userService.saveUser(new UserDto("Пользователь 3", "user3@yandex.ru"));
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(null, "Предмет 2", "Описание 2", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto1, user2.getId());
        itemService.saveItem(itemDto2, user3.getId());
        BookingResearchDto bookingResearchDto1 = new BookingResearchDto(LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), 1L);
        BookingResearchDto bookingResearchDto2 = new BookingResearchDto(LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), 2L);
        BookingResearchDto bookingResearchDto3 = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(8), 2L);
        bookingService.saveBooking(bookingResearchDto1, user1.getId());
        bookingService.saveBooking(bookingResearchDto2, user1.getId());
        bookingService.saveBooking(bookingResearchDto3, user2.getId());
        bookingService.approvedOrRejectBooking(user2.getId(), 1L, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 2L, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 3L, true);
        List<BookingResponseDto> bookingResponseDtos = bookingService.getBookingToUser(1L,
                "CURRENT", 0, Long.MAX_VALUE);
        assertEquals(2,bookingResponseDtos.size());
    }

    @Test
    @DirtiesContext
    void getBookingToOwnerTest() {
        User user1 = userService.saveUser(new UserDto("Пользователь 1", "user1@yandex.ru"));
        User user2 = userService.saveUser(new UserDto("Пользователь 2", "user2@yandex.ru"));
        User user3 = userService.saveUser(new UserDto("Пользователь 3", "user3@yandex.ru"));
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(null, "Предмет 2", "Описание 2", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto3 = new ItemDto(null, "Предмет 3", "Описание 3", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto4 = new ItemDto(null, "Предмет 4", "Описание 4", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto5 = new ItemDto(null, "Предмет 5", "Описание 5", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto1, user2.getId());
        itemService.saveItem(itemDto2, user3.getId());
        itemService.saveItem(itemDto3, user3.getId());
        itemService.saveItem(itemDto4, user3.getId());
        itemService.saveItem(itemDto5, user1.getId());
        BookingResearchDto bookingResearchDto1 = new BookingResearchDto(LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), 1L);
        BookingResearchDto bookingResearchDto2 = new BookingResearchDto(LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), 2L);
        BookingResearchDto bookingResearchDto3 = new BookingResearchDto(LocalDateTime.now().plusDays(10),
                LocalDateTime.now(), 3L);
        BookingResearchDto bookingResearchDto4 = new BookingResearchDto(LocalDateTime.now().plusDays(100),
                LocalDateTime.now().plusDays(10), 4L);
        BookingResearchDto bookingResearchDto5 = new BookingResearchDto(LocalDateTime.now().plusDays(5),
                LocalDateTime.now(), 5L);
        bookingService.saveBooking(bookingResearchDto1, user1.getId());
        bookingService.saveBooking(bookingResearchDto2, user2.getId());
        bookingService.saveBooking(bookingResearchDto3, user1.getId());
        bookingService.saveBooking(bookingResearchDto4, user1.getId());
        bookingService.saveBooking(bookingResearchDto5, user3.getId());
        bookingService.approvedOrRejectBooking(user2.getId(), 1L, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 2L, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 3L, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 4L, true);
        bookingService.approvedOrRejectBooking(user1.getId(), 5L, true);
        List<BookingResponseDto> bookingResponseDtos = bookingService.getBookingToOwner(3L,
                "CURRENT", 0, Long.MAX_VALUE);
        assertEquals(2,bookingResponseDtos.size());
    }
}