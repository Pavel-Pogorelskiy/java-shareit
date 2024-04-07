package ru.practicum.shareit.item.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.response.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class CommentRepositoryJpaTest {

    @Autowired
    private CommentRepositoryJpa repositoryJpa;
    @Autowired
    private UserRepositoryJpa userRepositoryJpa;
    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;


    @Test
    @DirtiesContext
    void findCommentByItem_IdTestOneItem() {
        User user = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user, null);
        userRepositoryJpa.save(user);
        userRepositoryJpa.save(user2);
        itemRepositoryJpa.save(item1);
        Comment comment = new Comment(null, "Текст", user2, item1, LocalDateTime.now());
        repositoryJpa.save(comment);
        List<Comment> comments = repositoryJpa.findCommentByItem_Id(1L);
        assertEquals(1,comments.size());
    }

    @Test
    @DirtiesContext
    void findCommentByItem_IdTestEmpty() {
        User user = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user, null);
        userRepositoryJpa.save(user);
        userRepositoryJpa.save(user2);
        itemRepositoryJpa.save(item1);
        Comment comment = new Comment(null, "Текст", user2, item1, LocalDateTime.now());
        repositoryJpa.save(comment);
        List<Comment> comments = repositoryJpa.findCommentByItem_Id(2L);
        assertEquals(0,comments.size());
    }

    @Test
    @DirtiesContext
    void findCommentByItem_IdTestTwoItem() {
        User user = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(null, "Пользователь 3", "user3@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user, null);
        userRepositoryJpa.save(user);
        userRepositoryJpa.save(user2);
        userRepositoryJpa.save(user3);
        itemRepositoryJpa.save(item1);
        Comment comment1 = new Comment(null, "Текст", user2, item1, LocalDateTime.now());
        Comment comment2 = new Comment(null, "Текст", user3, item1, LocalDateTime.now());
        repositoryJpa.save(comment1);
        repositoryJpa.save(comment2);
        List<Comment> comments = repositoryJpa.findCommentByItem_Id(1L);
        assertEquals(2,comments.size());
    }
}