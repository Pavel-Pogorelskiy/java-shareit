package ru.practicum.shareit.booking.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.response.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.response.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryJpaTest {

    @Autowired
    private BookingRepositoryJpa repositoryJpa;
    @Autowired
    private UserRepositoryJpa userRepositoryJpa;
    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;

    @Test
    @DirtiesContext
    void findBookingByBooker_IdAndStatusTestTwoRequestTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Предмет 2", "Описание 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        Item saveItem2 = itemRepositoryJpa.save(item2);
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem2, Status.APPROVED, created2, end2);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        List<Booking> bookingActual = repositoryJpa.findBookingByBooker_IdAndStatus(saveUser2.getId(),
                Status.APPROVED);
        assertEquals(2, bookingActual.size());
        assertTrue(bookingActual.contains(booking1Save));
        assertTrue(bookingActual.contains(booking2Save));
    }

    @Test
    @DirtiesContext
    void findBookingByBooker_IdAndStatusTestOneRequestTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Предмет 2", "Описание 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        Item saveItem2 = itemRepositoryJpa.save(item2);
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.REJECTED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem2, Status.APPROVED, created2, end2);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        List<Booking> bookingActual = repositoryJpa.findBookingByBooker_IdAndStatus(saveUser2.getId(),
                Status.APPROVED);
        assertEquals(1, bookingActual.size());
        assertTrue(bookingActual.contains(booking2Save));
    }

    @Test
    @DirtiesContext
    void findBookingByBooker_IdOrderByStartDescTestThreeRequestTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Предмет 2", "Описание 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        Item saveItem2 = itemRepositoryJpa.save(item2);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem2, Status.APPROVED, created2, end2);
        Booking booking3 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created3, end3);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        Booking booking3Save = repositoryJpa.save(booking3);
        List<Booking> bookingActual = repositoryJpa.findBookingByBooker_IdOrderByStartDesc(saveUser2.getId());
        assertEquals(3, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking3Save);
        assertEquals(bookingActual.get(2), booking2Save);
    }

    @Test
    @DirtiesContext
    void findBookingByBooker_IdOrderByStartDescTestTwoRequestTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Предмет 2", "Описание 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        Item saveItem2 = itemRepositoryJpa.save(item2);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.REJECTED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem2, Status.WAITING, created2, end2);
        Booking booking3 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created3, end3);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        Booking booking3Save = repositoryJpa.save(booking3);
        List<Booking> bookingActual = repositoryJpa.findBookingByBooker_IdOrderByStartDesc(saveUser2.getId());
        assertEquals(3, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking3Save);
        assertEquals(bookingActual.get(2), booking2Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItem_Owner_IdAndStatusTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(null, "Пользователь 3", "user3@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        User saveUser3 = userRepositoryJpa.save(user3);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Предмет 2", "Описание 2", true, saveUser1,
                null);
        Item item3 = new Item(null, "Предмет 3", "Описание 3", true, saveUser3,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        Item saveItem2 = itemRepositoryJpa.save(item2);
        Item saveItem3 = itemRepositoryJpa.save(item3);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem2, Status.WAITING, created2, end2);
        Booking booking3 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created3, end3);
        Booking booking4 = new Booking(null, saveUser2, saveItem3, Status.REJECTED, created4, end4);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        Booking booking3Save = repositoryJpa.save(booking3);
        Booking booking4Save = repositoryJpa.save(booking4);
        List<Booking> bookingActual = repositoryJpa.findBookingByItem_Owner_IdAndStatus(saveItem1.getId(),
                Status.APPROVED);
        assertEquals(2, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking3Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItem_Owner_IdOrderByStartDescTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(null, "Пользователь 3", "user3@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        User saveUser3 = userRepositoryJpa.save(user3);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Предмет 2", "Описание 2", true, saveUser1,
                null);
        Item item3 = new Item(null, "Предмет 3", "Описание 3", true, saveUser3,
                null);
        Item item4 = new Item(null, "Предмет 4", "Описание 4", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        Item saveItem2 = itemRepositoryJpa.save(item2);
        Item saveItem3 = itemRepositoryJpa.save(item3);
        Item saveItem4 = itemRepositoryJpa.save(item4);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        LocalDateTime created5 = LocalDateTime.now().plusDays(75);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem2, Status.WAITING, created2, end2);
        Booking booking3 = new Booking(null, saveUser3, saveItem1, Status.APPROVED, created3, end3);
        Booking booking4 = new Booking(null, saveUser2, saveItem3, Status.REJECTED, created4, end4);
        Booking booking5 = new Booking(null, saveUser3, saveItem4, Status.APPROVED, created5, end5);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        Booking booking3Save = repositoryJpa.save(booking3);
        Booking booking4Save = repositoryJpa.save(booking4);
        Booking booking5Save = repositoryJpa.save(booking5);
        List<Booking> bookingActual = repositoryJpa.findBookingByItem_Owner_IdOrderByStartDesc(
                saveItem1.getOwner().getId());
        assertEquals(4, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking5Save);
        assertEquals(bookingActual.get(2), booking3Save);
        assertEquals(bookingActual.get(3), booking2Save);
    }

    @Test
    void findBookingByItemAndStartAfterTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(null, "Пользователь 3", "user3@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        User saveUser3 = userRepositoryJpa.save(user3);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        LocalDateTime created5 = LocalDateTime.now().minusDays(1);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created2, end2);
        Booking booking3 = new Booking(null, saveUser3, saveItem1, Status.APPROVED, created3, end3);
        Booking booking4 = new Booking(null, saveUser2, saveItem1, Status.REJECTED, created4, end4);
        Booking booking5 = new Booking(null, saveUser3, saveItem1, Status.APPROVED, created5, end5);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        Booking booking3Save = repositoryJpa.save(booking3);
        Booking booking4Save = repositoryJpa.save(booking4);
        Booking booking5Save = repositoryJpa.save(booking5);
        List<Booking> bookingActual = repositoryJpa.findBookingByItemAndStartAfter(saveItem1.getId(),
                Status.APPROVED);
        assertEquals(5, repositoryJpa.findAll().size());
        assertEquals(3, bookingActual.size());
        assertEquals(bookingActual.get(0), booking2Save);
        assertEquals(bookingActual.get(1), booking3Save);
        assertEquals(bookingActual.get(2), booking1Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItemAndStartBeforeTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(null, "Пользователь 3", "user3@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        User saveUser3 = userRepositoryJpa.save(user3);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        LocalDateTime created5 = LocalDateTime.now().minusDays(1);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created2, end2);
        Booking booking3 = new Booking(null, saveUser3, saveItem1, Status.APPROVED, created3, end3);
        Booking booking4 = new Booking(null, saveUser2, saveItem1, Status.REJECTED, created4, end4);
        Booking booking5 = new Booking(null, saveUser3, saveItem1, Status.APPROVED, created5, end5);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        Booking booking3Save = repositoryJpa.save(booking3);
        Booking booking4Save = repositoryJpa.save(booking4);
        Booking booking5Save = repositoryJpa.save(booking5);
        List<Booking> bookingActual = repositoryJpa.findBookingByItemAndStartBefore(saveItem1.getId(),
                Status.APPROVED);
        assertEquals(5, repositoryJpa.findAll().size());
        assertEquals(1, bookingActual.size());
        assertEquals(bookingActual.get(0), booking5Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatusTest() {
        User user1 = new User(null, "Пользователь 1", "user1@yandex.ru");
        User user2 = new User(null, "Пользователь 2", "user2@yandex.ru");
        User user3 = new User(null, "Пользователь 3", "user3@yandex.ru");
        User saveUser1 = userRepositoryJpa.save(user1);
        User saveUser2 = userRepositoryJpa.save(user2);
        User saveUser3 = userRepositoryJpa.save(user3);
        Item item1 = new Item(null, "Предмет 1", "Описание 1", true, saveUser1,
                null);
        Item saveItem1 = itemRepositoryJpa.save(item1);
        LocalDateTime created1 = LocalDateTime.now().minusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().minusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(49);
        LocalDateTime created4 = LocalDateTime.now().minusDays(2);
        LocalDateTime end4 = created2.plusDays(1);
        LocalDateTime created5 = LocalDateTime.now().minusDays(1);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created1, end1);
        Booking booking2 = new Booking(null, saveUser2, saveItem1, Status.APPROVED, created2, end2);
        Booking booking3 = new Booking(null, saveUser3, saveItem1, Status.APPROVED, created3, end3);
        Booking booking4 = new Booking(null, saveUser2, saveItem1, Status.REJECTED, created4, end4);
        Booking booking5 = new Booking(null, saveUser3, saveItem1, Status.APPROVED, created5, end5);
        Booking booking1Save = repositoryJpa.save(booking1);
        Booking booking2Save = repositoryJpa.save(booking2);
        Booking booking3Save = repositoryJpa.save(booking3);
        Booking booking4Save = repositoryJpa.save(booking4);
        Booking booking5Save = repositoryJpa.save(booking5);
        List<Booking> bookingActual = repositoryJpa.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(1L,
                2L, LocalDateTime.now(), Status.APPROVED);
        assertEquals(5, repositoryJpa.findAll().size());
        assertEquals(1, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
    }
}