package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    void saveUserTest() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        User userExpected = new User(1L,"Пользователь 1", "user1@yandex.ru");
        User user = userService.saveUser(userDto);
        assertEquals(userExpected.getId(), user.getId());
        assertEquals(userExpected.getName(), user.getName());
        assertEquals(userExpected.getEmail(), user.getEmail());
    }

    @Test
    @DirtiesContext
    void updateUserTestThrowsNotFoundDataException() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto("Пользователь обновленный 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> userService.updateUser(2L, userUpdateDto));
        assertEquals("Пользователя с id = " + 2 + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void updateUserTestName() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto("Пользователь обновленный 1", null);
        userService.saveUser(userDto);
        User userUpdate = userService.updateUser(1L, userUpdateDto);
        User userExpected = new User(1L,"Пользователь обновленный 1","user1@yandex.ru");
        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void updateUserTestEmail() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(null, "userupdate1@yandex.ru");
        userService.saveUser(userDto);
        User userUpdate = userService.updateUser(1L, userUpdateDto);
        User userExpected = new User(1L,"Пользователь 1","userupdate1@yandex.ru");
        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void updateUserTestNameAndEmail() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto("Пользователь обновленный 1", "userupdate1@yandex.ru");
        userService.saveUser(userDto);
        User userUpdate = userService.updateUser(1L, userUpdateDto);
        User userExpected = new User(1L,"Пользователь обновленный 1","userupdate1@yandex.ru");
        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void updateUserTestNull() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(null, null);
        userService.saveUser(userDto);
        User userUpdate = userService.updateUser(1L, userUpdateDto);
        User userExpected = new User(1L,"Пользователь 1", "user1@yandex.ru");
        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void removeUserTest() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        assertEquals(1, userService.getAllUser().size());
        userService.removeUser(1L);
        assertEquals(0, userService.getAllUser().size());
    }

    @Test
    @DirtiesContext
    void removeUserTestThrowsNotFoundDataException() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        assertEquals(1, userService.getAllUser().size());
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> userService.removeUser(2L)
        );
        assertEquals("Пользователя с id = " + 2 + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getUserTestNotFoundDataException() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> userService.getUser(2L)
        );
        assertEquals("Пользователя с id = " + 2 + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getUserTest() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        User user = userService.getUser(1L);
        User userExpected = new User(1L, "Пользователь 1", "user1@yandex.ru");
        assertEquals(userExpected.getId(), user.getId());
        assertEquals(userExpected.getName(), user.getName());
        assertEquals(userExpected.getEmail(), user.getEmail());
    }

    @Test
    @DirtiesContext
    void getUserTestToRemoveNotFoundDataException() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        userService.saveUser(userDto);
        User user = userService.getUser(1L);
        assertEquals(1, userService.getAllUser().size());
        userService.removeUser(1L);
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> userService.getUser(1L)
        );
        assertEquals("Пользователя с id = " + 1 + " не найден", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getAllUserTestNull() {
        assertEquals(0, userService.getAllUser().size());
    }

    @Test
    @DirtiesContext
    void getAllUserTestTwo() {
        UserDto user1Dto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto user2Dto = new UserDto("Пользователь 2", "user2@yandex.ru");
        userService.saveUser(user1Dto);
        userService.saveUser(user2Dto);
        assertEquals(2, userService.getAllUser().size());
    }

    @Test
    @DirtiesContext
    void getAllUserTestOne() {
        UserDto user1Dto = new UserDto("Пользователь 1", "user1@yandex.ru");
        UserDto user2Dto = new UserDto("Пользователь 2", "user2@yandex.ru");
        userService.saveUser(user1Dto);
        userService.saveUser(user2Dto);
        assertEquals(2, userService.getAllUser().size());
        userService.removeUser(1L);
        assertEquals(1, userService.getAllUser().size());
    }
}