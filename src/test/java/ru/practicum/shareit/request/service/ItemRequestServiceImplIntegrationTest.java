package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToRequestResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;
import ru.practicum.shareit.request.response.ItemRequestJpa;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRequestJpa requestJpa;

    @Test
    @DirtiesContext
    void saveItemRequestTestThrowsNotFoundDataException() {
        long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Описание 1");
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.saveItemRequest(userId, requestDto));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
        assertEquals(0, requestJpa.findAll().size());
    }

    @Test
    @DirtiesContext
    void saveItemRequestTest() {
        long userId = 1L;
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Описание 1");
        ItemRequestsResponseDto itemRequestsExpected = new ItemRequestsResponseDto(1L, "Описание 1",
                requestDto.getCreated(), new ArrayList<>());
        ItemRequestsResponseDto itemRequestsActual = requestService.saveItemRequest(userId, requestDto);
        assertEquals(1, requestJpa.findAll().size());
        assertEquals(itemRequestsExpected.getId(), itemRequestsActual.getId());
        assertEquals(itemRequestsExpected.getDescription(), itemRequestsActual.getDescription());
        assertEquals(itemRequestsExpected.getCreated(), itemRequestsActual.getCreated());
        assertEquals(itemRequestsExpected.getItems().size(), itemRequestsActual.getItems().size());
    }

    @Test
    @DirtiesContext
    void getRequestsToUserTestTwoRequest() {
        long user1Id = 1L;
        long user2Id = 2L;
        UserDto user1Dto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto user2Dto = new UserDto("Пользователь 2", "user2@yandex.ru");
        userService.saveUser(user1Dto);
        userService.saveUser(user2Dto);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Описание 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Описание 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Описание 3");
        long requestId = 3L;
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user2Id, requestDto2);
        requestService.saveItemRequest(user1Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        List<ItemToRequestResponse> itemsResponseRequest3 = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1L, "Предмет 1",
                "Описание 1", true, requestId);
        itemsResponseRequest3.add(itemResponse);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1L, "Описание 1",
                requestDto1.getCreated(), new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(3L, "Описание 3",
                requestDto3.getCreated(), itemsResponseRequest3);
        List<ItemRequestsResponseDto> requestsActual = requestService.getRequestsToUser(user1Id);
        assertEquals(3, requestJpa.findAll().size());
        assertEquals(2, requestsActual.size());
        assertEquals(requestResponseDto1.getId(), requestsActual.get(1).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsActual.get(1).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsActual.get(1).getCreated());
        assertEquals(requestResponseDto1.getItems().size(), requestsActual.get(1).getItems().size());
        assertEquals(requestResponseDto2.getId(), requestsActual.get(0).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsActual.get(0).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsActual.get(0).getCreated());
        assertEquals(requestResponseDto2.getItems().size(), requestsActual.get(0).getItems().size());
        assertEquals(requestResponseDto2.getItems().get(0).getId(),
                requestsActual.get(0).getItems().get(0).getId());
        assertEquals(requestResponseDto2.getItems().get(0).getName(),
                requestsActual.get(0).getItems().get(0).getName());
        assertEquals(requestResponseDto2.getItems().get(0).getDescription(),
                requestsActual.get(0).getItems().get(0).getDescription());
        assertEquals(requestResponseDto2.getItems().get(0).getAvailable(),
                requestsActual.get(0).getItems().get(0).getAvailable());
        assertEquals(requestResponseDto2.getItems().get(0).getRequestId(),
                requestsActual.get(0).getItems().get(0).getRequestId());
    }

    @Test
    @DirtiesContext
    void getRequestsToUserTestThrowsNotFoundDataException() {
        long user1Id = 1L;
        long user2Id = 2L;
        long user3Id = 3L;
        UserDto user1Dto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto user2Dto = new UserDto("Пользователь 2", "user2@yandex.ru");
        userService.saveUser(user1Dto);
        userService.saveUser(user2Dto);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Описание 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Описание 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Описание 3");
        long requestId = 3L;
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user2Id, requestDto2);
        requestService.saveItemRequest(user1Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.getRequestsToUser(user3Id));
        assertEquals("Пользователя с id = " + user3Id + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getRequestsToAnotherUsersTestThrowsNotFoundDataException() {
        long user1Id = 1L;
        long user2Id = 2L;
        long user3Id = 3L;
        UserDto user1Dto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto user2Dto = new UserDto("Пользователь 2", "user2@yandex.ru");
        userService.saveUser(user1Dto);
        userService.saveUser(user2Dto);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Описание 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Описание 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Описание 3");
        long requestId = 3L;
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user1Id, requestDto2);
        requestService.saveItemRequest(user2Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.getRequestsToUser(user3Id));
        assertEquals("Пользователя с id = " + user3Id + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getRequestsToAnotherUsersTest() {
        long user1Id = 1L;
        long user2Id = 2L;
        long user3Id = 3L;
        UserDto user1Dto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto user2Dto = new UserDto("Пользователь 2", "user2@yandex.ru");
        UserDto user3Dto = new UserDto("Пользователь 3", "user3@yandex.ru");
        userService.saveUser(user1Dto);
        userService.saveUser(user2Dto);
        userService.saveUser(user3Dto);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Описание 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Описание 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Описание 3");
        long requestId = 3L;
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user3Id, requestDto2);
        requestService.saveItemRequest(user2Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        List<ItemToRequestResponse> itemsResponseRequest3 = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1L, "Предмет 1",
                "Описание 1", true, requestId);
        itemsResponseRequest3.add(itemResponse);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1L, "Описание 1",
                requestDto1.getCreated(), new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto3 = new ItemRequestsResponseDto(3L, "Описание 3",
                requestDto3.getCreated(), itemsResponseRequest3);
        List<ItemRequestsResponseDto> requestsActual = requestService
                .getRequestsToAnotherUsers(user3Id, 0, 20);
        assertEquals(3, requestJpa.findAll().size());
        assertEquals(2, requestsActual.size());
        assertEquals(requestResponseDto1.getId(), requestsActual.get(1).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsActual.get(1).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsActual.get(1).getCreated());
        assertEquals(requestResponseDto1.getItems().size(), requestsActual.get(1).getItems().size());
        assertEquals(requestResponseDto3.getId(), requestsActual.get(0).getId());
        assertEquals(requestResponseDto3.getDescription(), requestsActual.get(0).getDescription());
        assertEquals(requestResponseDto3.getCreated(), requestsActual.get(0).getCreated());
        assertEquals(requestResponseDto3.getItems().size(), requestsActual.get(0).getItems().size());
        assertEquals(requestResponseDto3.getItems().get(0).getId(),
                requestsActual.get(0).getItems().get(0).getId());
        assertEquals(requestResponseDto3.getItems().get(0).getName(),
                requestsActual.get(0).getItems().get(0).getName());
        assertEquals(requestResponseDto3.getItems().get(0).getDescription(),
                requestsActual.get(0).getItems().get(0).getDescription());
        assertEquals(requestResponseDto3.getItems().get(0).getAvailable(),
                requestsActual.get(0).getItems().get(0).getAvailable());
        assertEquals(requestResponseDto3.getItems().get(0).getRequestId(),
                requestsActual.get(0).getItems().get(0).getRequestId());
    }

    @Test
    @DirtiesContext
    void getRequestsToAnotherUsersTestSize() {
        long user1Id = 1L;
        long user2Id = 2L;
        long user3Id = 3L;
        UserDto user1Dto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto user2Dto = new UserDto("Пользователь 2", "user2@yandex.ru");
        UserDto user3Dto = new UserDto("Пользователь 3", "user3@yandex.ru");
        userService.saveUser(user1Dto);
        userService.saveUser(user2Dto);
        userService.saveUser(user3Dto);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Описание 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Описание 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Описание 3");
        long requestId = 3L;
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user3Id, requestDto2);
        requestService.saveItemRequest(user2Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        List<ItemToRequestResponse> itemsResponseRequest3 = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1L, "Предмет 1",
                "Описание 1", true, requestId);
        itemsResponseRequest3.add(itemResponse);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1L, "Описание 1",
                requestDto1.getCreated(), new ArrayList<>());
        List<ItemRequestsResponseDto> requestsActual = requestService
                .getRequestsToAnotherUsers(user3Id, 1, 1);
        assertEquals(3, requestJpa.findAll().size());
        assertEquals(1, requestsActual.size());
        assertEquals(requestResponseDto1.getId(), requestsActual.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsActual.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsActual.get(0).getCreated());
        assertEquals(requestResponseDto1.getItems().size(), requestsActual.get(0).getItems().size());
    }

    @Test
    @DirtiesContext
    void getRequestTestThrowsNotFoundDataException() {
        long userId = 1L;
        long requestId = 3L;
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Описание 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Описание 2");
        requestService.saveItemRequest(userId, requestDto1);
        requestService.saveItemRequest(userId, requestDto2);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.getRequest(userId, requestId));
        assertEquals("Запроса с id = " + requestId + " не существует", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getRequestTest() {
        long userId = 1L;
        long requestId = 2L;
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Описание 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Описание 2");
        requestService.saveItemRequest(userId, requestDto1);
        requestService.saveItemRequest(userId, requestDto2);
        ItemRequestsResponseDto itemRequestsExpected = new ItemRequestsResponseDto(2L, "Описание 2",
                requestDto2.getCreated(), new ArrayList<>());
        ItemRequestsResponseDto itemRequestsActual = requestService.getRequest(userId, requestId);
        assertEquals(itemRequestsExpected.getId(), itemRequestsActual.getId());
        assertEquals(itemRequestsExpected.getDescription(), itemRequestsActual.getDescription());
        assertEquals(itemRequestsExpected.getCreated(), itemRequestsActual.getCreated());
        assertEquals(itemRequestsExpected.getItems().size(), itemRequestsActual.getItems().size());
    }
}