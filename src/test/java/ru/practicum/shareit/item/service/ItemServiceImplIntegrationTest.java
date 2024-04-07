package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseToItemDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AnotherUserException;
import ru.practicum.shareit.exception.NotBookingException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.response.ItemRepositoryJpa;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRepositoryJpa repositoryJpa;
    @Autowired
    private BookingService bookingService;

    @Test
    @DirtiesContext
    void getItemTest() {
        long userId = 1L;
        long itemId = 1L;
        LocalDateTime endLast = LocalDateTime.now().minusDays(1);
        LocalDateTime startLast = LocalDateTime.now().minusDays(2);
        BookingResearchDto bookingResearchDto = new BookingResearchDto(endLast, startLast, itemId);
        LocalDateTime endNext = LocalDateTime.now().plusDays(20);
        LocalDateTime startNext = LocalDateTime.now().plusDays(10);
        BookingResearchDto bookingResearchDtoNext = new BookingResearchDto(endNext, startNext, itemId);
        UserDto userDto1 = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userDto2 = new UserDto("Пользователь 2", "user2@yandex.ru");
        UserDto userDto3 = new UserDto("Пользователь 3", "user3@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        LocalDateTime createdComment = LocalDateTime.now();
        CommentResearchDto commentResearchDto = new CommentResearchDto("Комментарий 1", createdComment);
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        itemService.saveItem(itemDto1, userId);
        bookingService.saveBooking(bookingResearchDto, 2L);
        bookingService.approvedOrRejectBooking(userId, 1L, true);
        bookingService.saveBooking(bookingResearchDtoNext, 3L);
        bookingService.approvedOrRejectBooking(userId, 2L, true);
        itemService.saveComment(itemId,2L, commentResearchDto);
        List<CommentResponseDto> commentsResponse = new ArrayList<>();
        CommentResponseDto commentResponse = new CommentResponseDto(1L, "Комментарий 1",
                "Пользователь 2", createdComment);
        commentsResponse.add(commentResponse);
        ItemDto itemResponse = itemService.getItem(itemId, userId);
        ItemDto itemResponseExpected = new ItemDto(1L, "Предмет 1", "Описание 1", true,
                new BookingResponseToItemDto(2L, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endNext),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startNext), 3L),
                new BookingResponseToItemDto(1L, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endLast),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startLast), 2L),
                commentsResponse, null);
        assertEquals(itemResponseExpected.getId(), itemResponse.getId());
        assertEquals(itemResponseExpected.getName(), itemResponse.getName());
        assertEquals(itemResponseExpected.getDescription(), itemResponse.getDescription());
        assertEquals(itemResponseExpected.getAvailable(), itemResponse.getAvailable());
        assertEquals(itemResponseExpected.getNextBooking(), itemResponse.getNextBooking());
        assertEquals(itemResponseExpected.getLastBooking(), itemResponse.getLastBooking());
        assertEquals(itemResponseExpected.getComments(), itemResponse.getComments());
        assertEquals(itemResponseExpected.getRequestId(), itemResponse.getRequestId());
    }

    @Test
    @DirtiesContext
    void getItemTestThrowsNotFoundDataException() {
        long userId = 1L;
        long itemId = 1L;
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.getItem(itemId, userId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getItemToUserTestThrowsNotFoundDataException() {
        long userId = 1L;
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.getItemToUser(userId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getItemToUserTest() {
        long userId = 1L;
        long itemId = 1L;
        LocalDateTime endLast = LocalDateTime.now().minusDays(1);
        LocalDateTime startLast = LocalDateTime.now().minusDays(2);
        BookingResearchDto bookingResearchDto = new BookingResearchDto(endLast, startLast, itemId);
        LocalDateTime endNext = LocalDateTime.now().plusDays(20);
        LocalDateTime startNext = LocalDateTime.now().plusDays(10);
        BookingResearchDto bookingResearchDtoNext = new BookingResearchDto(endNext, startNext, itemId);
        UserDto userDto1 = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userDto2 = new UserDto("Пользователь 2", "user2@yandex.ru");
        UserDto userDto3 = new UserDto("Пользователь 3", "user3@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        ItemDto itemDto2 = new ItemDto(null, "Предмет 2", "Описание 2", true,
                null, null, null, null);
        LocalDateTime createdComment = LocalDateTime.now();
        CommentResearchDto commentResearchDto = new CommentResearchDto("Комментарий 1", createdComment);
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        itemService.saveItem(itemDto1, userId);
        itemService.saveItem(itemDto2, userId);
        bookingService.saveBooking(bookingResearchDto, 2L);
        bookingService.approvedOrRejectBooking(userId, 1L, true);
        bookingService.saveBooking(bookingResearchDtoNext, 3L);
        bookingService.approvedOrRejectBooking(userId, 2L, true);
        itemService.saveComment(itemId,2L, commentResearchDto);
        List<CommentResponseDto> commentsResponse = new ArrayList<>();
        CommentResponseDto commentResponse = new CommentResponseDto(1L, "Комментарий 1",
                "Пользователь 2", createdComment);
        commentsResponse.add(commentResponse);
        ItemDto item1ResponseExpected = new ItemDto(1L, "Предмет 1", "Описание 1", true,
                new BookingResponseToItemDto(2L, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endNext),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startNext), 3L),
                new BookingResponseToItemDto(1L, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endLast),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startLast), 2L),
                commentsResponse, null);
        ItemDto item2ResponseExpected = new ItemDto(2L, "Предмет 2", "Описание 2", true,
                null, null, new ArrayList<>(), null);
        List<ItemDto> itemsActual = itemService.getItemToUser(userId);
        assertEquals(2,itemsActual.size());
        assertEquals(item1ResponseExpected.getId(), itemsActual.get(0).getId());
        assertEquals(item1ResponseExpected.getName(), itemsActual.get(0).getName());
        assertEquals(item1ResponseExpected.getDescription(), itemsActual.get(0).getDescription());
        assertEquals(item1ResponseExpected.getAvailable(), itemsActual.get(0).getAvailable());
        assertEquals(item1ResponseExpected.getNextBooking(), itemsActual.get(0).getNextBooking());
        assertEquals(item1ResponseExpected.getLastBooking(), itemsActual.get(0).getLastBooking());
        assertEquals(item1ResponseExpected.getComments(), itemsActual.get(0).getComments());
        assertEquals(item1ResponseExpected.getRequestId(), itemsActual.get(0).getRequestId());
        assertEquals(item2ResponseExpected.getId(), itemsActual.get(1).getId());
        assertEquals(item2ResponseExpected.getName(), itemsActual.get(1).getName());
        assertEquals(item2ResponseExpected.getDescription(), itemsActual.get(1).getDescription());
        assertEquals(item2ResponseExpected.getAvailable(), itemsActual.get(1).getAvailable());
        assertEquals(item2ResponseExpected.getNextBooking(), itemsActual.get(1).getNextBooking());
        assertEquals(item2ResponseExpected.getLastBooking(), itemsActual.get(1).getLastBooking());
        assertEquals(item2ResponseExpected.getComments(), itemsActual.get(1).getComments());
        assertEquals(item2ResponseExpected.getRequestId(), itemsActual.get(1).getRequestId());
    }

    @Test
    @DirtiesContext
    void updateItemTest() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        userService.saveUser(userDto);
        itemService.saveItem(itemDto1, 1L);
        ItemDto itemUpdateDto = new ItemDto(null, "Предмет обновленный 1", "Описание новое 1",
                false, null, null, null, null);
        ItemDto itemResponse = itemService.updateItem(itemUpdateDto, 1L, 1L);
        ItemDto itemResponseExpected = new ItemDto(1L, "Предмет обновленный 1", "Описание новое 1",
                false, null, null, new ArrayList<>(), null);
        assertEquals(itemResponseExpected.getId(), itemResponse.getId());
        assertEquals(itemResponseExpected.getName(), itemResponse.getName());
        assertEquals(itemResponseExpected.getDescription(), itemResponse.getDescription());
        assertEquals(itemResponseExpected.getAvailable(), itemResponse.getAvailable());
        assertEquals(itemResponseExpected.getNextBooking(), itemResponse.getNextBooking());
        assertEquals(itemResponseExpected.getLastBooking(), itemResponse.getLastBooking());
        assertEquals(itemResponseExpected.getComments(), itemResponse.getComments());
        assertEquals(itemResponseExpected.getRequestId(), itemResponse.getRequestId());
    }

    @Test
    @DirtiesContext
    void updateItemTestNotFoundDataException() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        userService.saveUser(userDto);
        itemService.saveItem(itemDto1, 1L);
        ItemDto itemUpdateDto = new ItemDto(null, "Предмет обновленный 1", "Описание новое 1",
                false, null, null, null, null);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.updateItem(itemUpdateDto, 1L, 2L));
        assertEquals("Вещь с id = " + 2L + " не найдена", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void updateItemTestAnotherUserException() {
        UserDto userDto1 = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userDto2 = new UserDto("Пользователь 2", "user2@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        itemService.saveItem(itemDto1, 1L);
        ItemDto itemUpdateDto = new ItemDto(null, "Предмет обновленный 1", "Описание новое 1",
                false, null, null, null, null);
        AnotherUserException ex = assertThrows(
                AnotherUserException.class,
                () -> itemService.updateItem(itemUpdateDto, 2L, 1L));
        assertEquals("Пользователь с id = " + 2L + " не имеет права " +
                "на изменение вещи, так как не является ее владельцем", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveItemTest() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        userService.saveUser(userDto);
        ItemDto itemResponse = itemService.saveItem(itemDto1, 1L);
        ItemDto itemResponseExpected = new ItemDto(1L, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), null);
        assertEquals(itemResponseExpected.getId(), itemResponse.getId());
        assertEquals(itemResponseExpected.getName(), itemResponse.getName());
        assertEquals(itemResponseExpected.getDescription(), itemResponse.getDescription());
        assertEquals(itemResponseExpected.getAvailable(), itemResponse.getAvailable());
        assertEquals(itemResponseExpected.getNextBooking(), itemResponse.getNextBooking());
        assertEquals(itemResponseExpected.getLastBooking(), itemResponse.getLastBooking());
        assertEquals(itemResponseExpected.getComments(), itemResponse.getComments());
        assertEquals(itemResponseExpected.getRequestId(), itemResponse.getRequestId());
    }

    @Test
    @DirtiesContext
    void saveItemTestThrowsNotFoundDataException() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        userService.saveUser(userDto);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.saveItem(itemDto1, 2L));
        assertEquals("Пользователя с id = " + 2 + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void searchItemTest() {
        String search = "отвертка";
        long userid = 1L;
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Что то", "Отвертка", true,
                null, null, null, null);
        ItemDto itemDto2 = new ItemDto(null, "Железная отвертка", "что то", true,
                null, null, null, null);
        ItemDto itemDto3 = new ItemDto(null, "Деревяная отвертка", "Деревяная отвертка",
                true, null, null, null, null);
        ItemDto itemDto4 = new ItemDto(null, "Что то", "Что то",
                true, null, null, null, null);
        userService.saveUser(userDto);
        itemService.saveItem(itemDto1, userid);
        itemService.saveItem(itemDto2, userid);
        itemService.saveItem(itemDto3, userid);
        itemService.saveItem(itemDto4, userid);
        assertEquals(4, repositoryJpa.findAll().size());
        List<ItemDto> itemsActual = itemService.searchItem(search, userid);
        assertEquals(3, itemsActual.size());
    }

    @Test
    @DirtiesContext
    void searchItemTestThrowsNotFoundDataException() {
        String search = "отвертка";
        long userid = 1L;
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> itemService.searchItem(search, userid));
        assertEquals("Пользователя с id = " + userid + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveCommentTestNotBookingException() {
        long userId = 1L;
        long itemId = 1L;
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        CommentResearchDto commentResearchDto = new CommentResearchDto("Комментарий 1", LocalDateTime.now());
        userService.saveUser(userDto);
        itemService.saveItem(itemDto1, userId);
        NotBookingException ex = assertThrows(
                NotBookingException.class,
                () -> itemService.saveComment(itemId,userId, commentResearchDto));
        assertEquals("Завершенных бронирований для пользователя с id = " +
                userId + " по предмету с id = " + itemId + "не существует", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveCommentTest() {
        long userId = 1L;
        long itemId = 1L;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(2), itemId);
        UserDto userDto1 = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userDto2 = new UserDto("Пользователь 2", "user2@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, null);
        CommentResearchDto commentResearchDto = new CommentResearchDto("Комментарий 1", LocalDateTime.now());
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        itemService.saveItem(itemDto1, userId);
        bookingService.saveBooking(bookingResearchDto, 2L);
        bookingService.approvedOrRejectBooking(userId, 1L, true);
        CommentResponseDto commentResponse = itemService.saveComment(itemId,2L, commentResearchDto);
        CommentResponseDto commentResponseExpected = new CommentResponseDto(1L, "Комментарий 1",
                "Пользователь 2", commentResponse.getCreated());
        assertEquals(commentResponseExpected.getId(), commentResponse.getId());
        assertEquals(commentResponseExpected.getText(), commentResponse.getText());
        assertEquals(commentResponseExpected.getAuthorName(), commentResponse.getAuthorName());
        assertEquals(commentResponseExpected.getCreated(), commentResponse.getCreated());
    }
}