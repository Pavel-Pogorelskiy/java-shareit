package ru.practicum.shareit.request.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.response.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestJpaTest {

    @Autowired
    private ItemRequestJpa repository;

    @Autowired
    UserRepositoryJpa userRepositoryJpa;

    @Test
    @DirtiesContext
    void findItemRequestByOwner_IdOrderByCreatedDescTest() {
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now().plusDays(30);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(20);
        User user1 = new User(null, "Имя 1", "user1@yandex.ru");
        User user2 = new User(null, "Имя 2", "user2@yandex.ru");
        userRepositoryJpa.save(user1);
        userRepositoryJpa.save(user2);
        ItemRequest request1 = new ItemRequest(null, "Запрос 1", created1, user1);
        ItemRequest request2 = new ItemRequest(null, "Запрос 2", created2, user1);
        ItemRequest request3 = new ItemRequest(null, "Запрос 3", created3, user2);
        ItemRequest request4 = new ItemRequest(null, "Запрос 4", created4, user1);
        ItemRequest requestResponse1 = new ItemRequest(1L, "Запрос 1", created1, user1);
        ItemRequest requestResponse2 = new ItemRequest(2L, "Запрос 2", created2, user1);
        ItemRequest requestResponse3 = new ItemRequest(3L, "Запрос 3", created3, user2);
        ItemRequest requestResponse4 = new ItemRequest(4L, "Запрос 4", created4, user1);
        repository.save(request1);
        repository.save(request2);
        repository.save(request3);
        repository.save(request4);
        assertEquals(4, repository.findAll().size());
        List<ItemRequest> requestList = repository.findItemRequestByOwner_IdOrderByCreatedDesc(1L);
        assertEquals(3, requestList.size());
        assertEquals(requestResponse2.getId(), requestList.get(0).getId());
        assertEquals(requestResponse2.getDescription(), requestList.get(0).getDescription());
        assertEquals(requestResponse2.getCreated(), requestList.get(0).getCreated());
        assertEquals(requestResponse4.getId(), requestList.get(1).getId());
        assertEquals(requestResponse4.getDescription(), requestList.get(1).getDescription());
        assertEquals(requestResponse4.getCreated(), requestList.get(1).getCreated());
        assertEquals(requestResponse1.getId(), requestList.get(2).getId());
        assertEquals(requestResponse1.getDescription(), requestList.get(2).getDescription());
        assertEquals(requestResponse1.getCreated(), requestList.get(2).getCreated());
    }

    @Test
    @DirtiesContext
    void findItemRequestNotByOwner_IdOrderByCreatedDesc() {
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now().plusDays(30);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(20);
        User user1 = new User(null, "Имя 1", "user1@yandex.ru");
        User user2 = new User(null, "Имя 2", "user2@yandex.ru");
        User user3 = new User(null, "Имя 3", "user3@yandex.ru");
        userRepositoryJpa.save(user1);
        userRepositoryJpa.save(user2);
        userRepositoryJpa.save(user3);
        ItemRequest request1 = new ItemRequest(null, "Запрос 1", created1, user1);
        ItemRequest request2 = new ItemRequest(null, "Запрос 2", created2, user2);
        ItemRequest request3 = new ItemRequest(null, "Запрос 3", created3, user3);
        ItemRequest request4 = new ItemRequest(null, "Запрос 4", created4, user1);
        ItemRequest requestResponse2 = new ItemRequest(2L, "Запрос 2", created2, user2);
        ItemRequest requestResponse3 = new ItemRequest(3L, "Запрос 3", created3, user3);
        repository.save(request1);
        repository.save(request2);
        repository.save(request3);
        repository.save(request4);
        assertEquals(4, repository.findAll().size());
        List<ItemRequest> requestList = repository.findItemRequestNotByOwner_IdOrderByCreatedDesc(1L);
        assertEquals(2, requestList.size());
        assertEquals(requestResponse3.getId(), requestList.get(0).getId());
        assertEquals(requestResponse3.getDescription(), requestList.get(0).getDescription());
        assertEquals(requestResponse3.getCreated(), requestList.get(0).getCreated());
        assertEquals(requestResponse2.getId(), requestList.get(1).getId());
        assertEquals(requestResponse2.getDescription(), requestList.get(1).getDescription());
        assertEquals(requestResponse2.getCreated(), requestList.get(1).getCreated());
    }
}