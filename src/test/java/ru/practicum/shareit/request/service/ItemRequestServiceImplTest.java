package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.ItemToRequestResponse;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.response.ItemRepositoryJpa;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.response.ItemRequestJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService requestService;
    @Mock
    private ItemRequestJpa requestJpa;
    @Mock
    private ItemRepositoryJpa itemRepositoryJpa;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        requestService = new ItemRequestServiceImpl(requestJpa, itemRepositoryJpa, userService,
                new ItemRequestMapperImpl(), new ItemMapperImpl());
    }

    @Test
    void saveItemRequestTestThrowsNotFoundException() {
        long id = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Описание 1");
        when(userService.getUser(anyLong()))
                .thenThrow(new NotFoundDataException("Пользователя с id = " + id + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.saveItemRequest(id, requestDto));
        assertEquals("Пользователя с id = " + id + " не найден", ex.getMessage());
        verify(requestJpa, never()).save(any(ItemRequest.class));
    }

    @Test
    void saveItemRequestTest() {
        long id = 1L;
        User user = new User(1L, "Пользователь 1", "user1@yandex.ru");
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Описание 1");
        LocalDateTime created = requestDto.getCreated();
        ItemRequest requestSave = new ItemRequest(1L, "Описание 1", created, user);
        ItemRequestsResponseDto requestsExpected = new ItemRequestsResponseDto(1L, "Описание 1",
                created, new ArrayList<>());
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestJpa.save(any(ItemRequest.class))).thenReturn(requestSave);
        ItemRequestsResponseDto requestsActual = requestService.saveItemRequest(id, requestDto);
        assertEquals(requestsExpected.getId(), requestsActual.getId());
        assertEquals(requestsExpected.getDescription(), requestsActual.getDescription());
        assertEquals(requestsExpected.getCreated(), requestsActual.getCreated());
        assertEquals(requestsExpected.getItems(), requestsActual.getItems());
        verify(requestJpa, atLeast(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequestsToUserTestThrowsNotFoundDataExceptionUser() {
        long userId = 1L;
        when(userService.getUser(anyLong()))
                .thenThrow(new NotFoundDataException("Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.getRequestsToUser(userId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getRequestsToUserTestNullItems() {
        long userId = 1L;
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        User user = new User(1L, "Пользователь 1", "user1@yandex.ru");
        ItemRequest request1 = new ItemRequest(1L, "Описание 1", created1, user);
        ItemRequest request2 = new ItemRequest(2L, "Описание 2", created2, user);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestJpa.findItemRequestByOwner_IdOrderByCreatedDesc(anyLong())).thenReturn(requests);
        when(itemRepositoryJpa.findByRequest_Id(anyLong())).thenReturn(new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1L, "Описание 1",
                created1, new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2L, "Описание 2",
                created2, new ArrayList<>());
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToUser(userId);
        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(requestResponseDto1.getItems(), requestsResponseDto.get(0).getItems());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(requestResponseDto2.getItems(), requestsResponseDto.get(1).getItems());
    }

    @Test
    void getRequestsToUserTest() {
        long userId = 1L;
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        User user = new User(1L, "Пользователь 1", "user1@yandex.ru");
        ItemRequest request1 = new ItemRequest(1L, "Описание 1", created1, user);
        ItemRequest request2 = new ItemRequest(2L, "Описание 2", created2, user);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        Item item = new Item(1L, "Предмет 1", "Описание 1", true,
                user, request1);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemToRequestResponse> itemsResponse = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1L, "Предмет 1",
                "Описание 1", true, 1L);
        itemsResponse.add(itemResponse);
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestJpa.findItemRequestByOwner_IdOrderByCreatedDesc(anyLong())).thenReturn(requests);
        when(itemRepositoryJpa.findByRequest_Id(2L)).thenReturn(new ArrayList<>());
        when(itemRepositoryJpa.findByRequest_Id(1L)).thenReturn(items);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1L, "Описание 1",
                created1, itemsResponse);
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2L, "Описание 2",
                created2, new ArrayList<>());
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToUser(userId);
        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(1, requestsResponseDto.get(0).getItems().size());
        assertEquals(requestResponseDto1.getItems().get(0).getId(),
                requestsResponseDto.get(0).getItems().get(0).getId());
        assertEquals(requestResponseDto1.getItems().get(0).getName(),
                requestsResponseDto.get(0).getItems().get(0).getName());
        assertEquals(requestResponseDto1.getItems().get(0).getDescription(),
                requestsResponseDto.get(0).getItems().get(0).getDescription());
        assertEquals(requestResponseDto1.getItems().get(0).getAvailable(),
                requestsResponseDto.get(0).getItems().get(0).getAvailable());
        assertEquals(requestResponseDto1.getItems().get(0).getRequestId(),
                requestsResponseDto.get(0).getItems().get(0).getRequestId());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(requestResponseDto2.getItems(), requestsResponseDto.get(1).getItems());
    }

    @Test
    void getRequestsToAnotherUsersTestThrowsNotFoundDataException() {
        long userId = 1L;
        when(userService.getUser(anyLong()))
                .thenThrow(new NotFoundDataException("Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.getRequestsToAnotherUsers(1L, 0, 20));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getRequestsToAnotherUsersTestNotItems() {
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(3L, "Пользователь 3", "user3@yandex.ru");
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "Описание 1", created1, user2);
        ItemRequest request2 = new ItemRequest(2L, "Описание 2", created2, user3);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        when(userService.getUser(userId)).thenReturn(user1);
        when(requestJpa.findItemRequestNotByOwner_IdOrderByCreatedDesc(anyLong())).thenReturn(requests);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1L, "Описание 1",
                created1, new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2L, "Описание 2",
                created2, new ArrayList<>());
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToAnotherUsers(userId,
                0, 20);
        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(0, requestsResponseDto.get(0).getItems().size());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(0, requestsResponseDto.get(1).getItems().size());
    }

    @Test
    void getRequestsToAnotherUsersTestOneItems() {
        long userId = 1L;
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(3L, "Пользователь 3", "user3@yandex.ru");
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "Описание 1", created1, user2);
        ItemRequest request2 = new ItemRequest(2L, "Описание 2", created2, user3);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        Item item = new Item(1L, "Предмет 1", "Описание 1", true,
                user1, request2);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemToRequestResponse> itemsResponse = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1L, "Предмет 1",
                "Описание 1", true, 2L);
        itemsResponse.add(itemResponse);
        when(userService.getUser(userId)).thenReturn(user1);
        when(requestJpa.findItemRequestNotByOwner_IdOrderByCreatedDesc(anyLong())).thenReturn(requests);
        when(itemRepositoryJpa.findByRequest_Id(2L)).thenReturn(items);
        when(itemRepositoryJpa.findByRequest_Id(1L)).thenReturn(new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1L, "Описание 1",
                created1, new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2L, "Описание 2",
                created2, itemsResponse);
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToAnotherUsers(userId,
                0, 20);
        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(0, requestsResponseDto.get(0).getItems().size());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(1, requestsResponseDto.get(1).getItems().size());
        assertEquals(requestResponseDto2.getItems().get(0).getId(),
                requestsResponseDto.get(1).getItems().get(0).getId());
        assertEquals(requestResponseDto2.getItems().get(0).getName(),
                requestsResponseDto.get(1).getItems().get(0).getName());
        assertEquals(requestResponseDto2.getItems().get(0).getDescription(),
                requestsResponseDto.get(1).getItems().get(0).getDescription());
        assertEquals(requestResponseDto2.getItems().get(0).getAvailable(),
                requestsResponseDto.get(1).getItems().get(0).getAvailable());
        assertEquals(requestResponseDto2.getItems().get(0).getRequestId(),
                requestsResponseDto.get(1).getItems().get(0).getRequestId());
    }

    @Test
    void getRequestTestThrowsNotFoundDataExceptionUser() {
        long requestId = 1L;
        long userId = 2L;
        when(userService.getUser(anyLong()))
                .thenThrow(new NotFoundDataException("Пользователя с id = " + userId + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.getRequest(userId, requestId));
        assertEquals("Пользователя с id = " + userId + " не найден", ex.getMessage());
    }

    @Test
    void getRequestTestThrowsNotFoundDataExceptionRequest() {
        long requestId = 1L;
        long userId = 2L;
        User user = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestJpa.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> requestService.getRequest(userId, requestId));
        assertEquals("Запроса с id = " + requestId + " не существует", ex.getMessage());
    }

    @Test
    void getRequestTestNullItems() {
        long requestId = 1L;
        long userId = 2L;
        LocalDateTime created = LocalDateTime.now();
        User user = new User(1L, "Пользователь 1", "user1@yandex.ru");
        ItemRequest request = new ItemRequest(1L, "Описание 1", created, user);
        ItemRequestsResponseDto requestsExpected = new ItemRequestsResponseDto(1L, "Описание 1",
                created, new ArrayList<>());
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestJpa.findById(anyLong())).thenReturn(Optional.of(request));
        ItemRequestsResponseDto requestsActual = requestService.getRequest(userId, requestId);
        assertEquals(requestsExpected.getId(), requestsActual.getId());
        assertEquals(requestsExpected.getDescription(), requestsActual.getDescription());
        assertEquals(requestsExpected.getCreated(), requestsActual.getCreated());
        assertEquals(requestsExpected.getItems(), requestsActual.getItems());
    }

    @Test
    void getRequestTestOneItems() {
        long requestId = 1L;
        long userId = 2L;
        LocalDateTime created = LocalDateTime.now();
        User user = new User(2L, "Пользователь 2", "user2@yandex.ru");
        ItemRequest request = new ItemRequest(1L, "Описание 1", created, user);
        Item item = new Item(1L, "Предмет 1", "Описание 1", true,
                user, request);
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestJpa.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepositoryJpa.findByRequest_Id(anyLong())).thenReturn(items);
        List<ItemToRequestResponse> itemsResponse = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1L, "Предмет 1",
                "Описание 1", true, requestId);
        itemsResponse.add(itemResponse);
        ItemRequestsResponseDto requestsExpected = new ItemRequestsResponseDto(1L, "Описание 1",
                created, itemsResponse);
        ItemRequestsResponseDto requestsActual = requestService.getRequest(userId, requestId);
        assertEquals(requestsExpected.getId(), requestsActual.getId());
        assertEquals(requestsExpected.getDescription(), requestsActual.getDescription());
        assertEquals(requestsExpected.getCreated(), requestsActual.getCreated());
        assertEquals(1, requestsActual.getItems().size());
        assertEquals(requestsExpected.getItems().get(0).getId(),requestsActual.getItems().get(0).getId());
        assertEquals(requestsExpected.getItems().get(0).getName(),requestsActual.getItems().get(0).getName());
        assertEquals(requestsExpected.getItems().get(0).getDescription(),
                requestsActual.getItems().get(0).getDescription());
        assertEquals(requestsExpected.getItems().get(0).getAvailable(),
                requestsActual.getItems().get(0).getAvailable());
        assertEquals(requestsExpected.getItems().get(0).getRequestId(),
                requestsActual.getItems().get(0).getRequestId());
    }
}