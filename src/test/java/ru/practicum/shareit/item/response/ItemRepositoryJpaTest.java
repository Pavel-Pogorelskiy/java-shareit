package ru.practicum.shareit.item.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.response.ItemRequestJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.response.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryJpaTest {

    @Autowired
    private ItemRepositoryJpa repositoryJpa;
    @Autowired
    private UserRepositoryJpa userRepositoryJpa;
    @Autowired
    private ItemRequestJpa itemRequestJpa;

    @Test
    @DirtiesContext
    void findByNameAndDescriptionTestEmpty() {
        List<Item> items = repositoryJpa.findByNameAndDescription("я", "я");
        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    void findByNameAndDescriptionTestOneItem() {
        User user = new User(null, "Пользователь 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user, null);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user, null);
        userRepositoryJpa.save(user);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        assertEquals(2, repositoryJpa.findAll().size());
        List<Item> items = repositoryJpa.findByNameAndDescription("водяная", "водяная");
        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByNameAndDescriptionTestTwoItem() {
        User user = new User(null, "Пользователь 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user, null);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user, null);
        userRepositoryJpa.save(user);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        assertEquals(2, repositoryJpa.findAll().size());
        List<Item> items = repositoryJpa.findByNameAndDescription("ДРель", "ДРель");
        assertEquals(2, items.size());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscTestEmpty() {
        List<Item> items = repositoryJpa.findByOwnerIdOrderByIdAsc(1L);
        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscTestOneItem() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, null);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user2, null);
        userRepositoryJpa.save(user1);
        userRepositoryJpa.save(user2);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        List<Item> items = repositoryJpa.findByOwnerIdOrderByIdAsc(1L);
        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscTestTwoItem() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, null);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user1, null);
        userRepositoryJpa.save(user1);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        List<Item> items = repositoryJpa.findByOwnerIdOrderByIdAsc(1L);
        assertEquals(2, items.size());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscTestOneItemToDelete() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, null);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user1, null);
        userRepositoryJpa.save(user1);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        List<Item> items = repositoryJpa.findByOwnerIdOrderByIdAsc(1L);
        assertEquals(2, items.size());
        repositoryJpa.deleteById(2L);
        List<Item> itemsNew = repositoryJpa.findByOwnerIdOrderByIdAsc(1L);
        assertEquals(1, itemsNew.size());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdTestEmpty() {
        List<Item> items = repositoryJpa.findByRequest_Id(1L);
        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdTestOne() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        userRepositoryJpa.save(user1);
        ItemRequest request = new ItemRequest(null, "Просто", LocalDateTime.now(), user1);
        ItemRequest saveRequest = itemRequestJpa.save(request);
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, saveRequest);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user1, null);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        List<Item> items = repositoryJpa.findByRequest_Id(1L);
        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdTestOneToTwo() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        userRepositoryJpa.save(user1);
        userRepositoryJpa.save(user2);
        ItemRequest request1 = new ItemRequest(null, "Просто1", LocalDateTime.now(), user1);
        ItemRequest request2 = new ItemRequest(null, "Просто2", LocalDateTime.now(), user2);
        ItemRequest saveRequest1 = itemRequestJpa.save(request1);
        ItemRequest saveRequest2 = itemRequestJpa.save(request2);
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, saveRequest1);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user1, saveRequest2);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        List<Item> items = repositoryJpa.findByRequest_Id(1L);
        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdInTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        userRepositoryJpa.save(user1);
        userRepositoryJpa.save(user2);
        ItemRequest request1 = new ItemRequest(null, "Просто1", LocalDateTime.now(), user1);
        ItemRequest request2 = new ItemRequest(null, "Просто2", LocalDateTime.now(), user2);
        ItemRequest request3 = new ItemRequest(null, "Просто3", LocalDateTime.now(), user2);
        ItemRequest request4 = new ItemRequest(null, "Просто4", LocalDateTime.now(), user1);
        ItemRequest request5 = new ItemRequest(null, "Просто5", LocalDateTime.now(), user1);
        ItemRequest saveRequest1 = itemRequestJpa.save(request1);
        ItemRequest saveRequest2 = itemRequestJpa.save(request2);
        ItemRequest saveRequest3 = itemRequestJpa.save(request3);
        ItemRequest saveRequest4 = itemRequestJpa.save(request4);
        ItemRequest saveRequest5 = itemRequestJpa.save(request5);
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, saveRequest1);
        Item item2 = new Item(null, "Водяная дрель", "Водяная дрель",
                true, user1, saveRequest2);
        Item item3 = new Item(null, "Молоток", "Молоток",
                true, user1, saveRequest3);
        Item item4 = new Item(null, "Питолет", "Игрушечный пистолет",
                true, user1, saveRequest4);
        Item item5 = new Item(null, "Макбук", "Макбук 2020 года",
                true, user1, saveRequest5);
        repositoryJpa.save(item1);
        repositoryJpa.save(item2);
        repositoryJpa.save(item3);
        repositoryJpa.save(item4);
        repositoryJpa.save(item5);
        List<Item> items = repositoryJpa.findByRequest_IdIn(List.of(1L, 3L, 5L));
        assertEquals(5, repositoryJpa.findAll().size());
        assertEquals(3, items.size());
    }
}