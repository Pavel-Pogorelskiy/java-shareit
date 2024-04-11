package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.response.UserRepositoryJpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;
    @Mock
    private UserRepositoryJpa repositoryJpa;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(new UserMapperImpl(), repositoryJpa);
    }

    @Test
    void saveUserTest() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        User user1 = new User(1L, "Пользователь 1", "user1@yandex.ru");
        when(repositoryJpa.save(any(User.class))).thenReturn(user1);
        User user = userService.saveUser(userDto);
        assertEquals(user1, user);
        verify(repositoryJpa).save(any(User.class));
    }

    @Test
    void updateUserTestThrowsNotFoundDataException() {
        UserDto userDto = new UserDto("Пользователь 1", "user1@yandex.ru");
        Long id = 1L;
        when(repositoryJpa.existsById(anyLong()))
                .thenThrow(new NotFoundDataException("Пользователя с id = " + id + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> userService.updateUser(id, userDto));
         assertEquals("Пользователя с id = " + id + " не найден", ex.getMessage());
    }

    @Test
    void updateUserTestName() {
        UserDto userDto = new UserDto("Пользователь 1 обновленный", null);
        Long id = 1L;
        Optional<User> lastUser = Optional.of(new User(1L, "Пользователь 1", "user1@yandex.ru"));
        User userUpdate = new User(id,"Пользователь 1 обновленный", "user1@yandex.ru");
        when(repositoryJpa.existsById(anyLong())).thenReturn(true);
        when(repositoryJpa.findById(anyLong())).thenReturn(lastUser);
        when(repositoryJpa.save(any(User.class))).thenReturn(userUpdate);
        assertEquals(userUpdate, userService.updateUser(id, userDto));
        verify(repositoryJpa).save(any(User.class));
    }

    @Test
    void updateUserTestEmail() {
        UserDto userDto = new UserDto(null, "user1update@yandex.ru");
        Long id = 1L;
        Optional<User> lastUser = Optional.of(new User(1L, "Пользователь 1", "user1@yandex.ru"));
        User userUpdate = new User(id,"Пользователь 1", "user1update@yandex.ru");
        when(repositoryJpa.existsById(anyLong())).thenReturn(true);
        when(repositoryJpa.findById(anyLong())).thenReturn(lastUser);
        when(repositoryJpa.save(any(User.class))).thenReturn(userUpdate);
        assertEquals(userUpdate, userService.updateUser(id, userDto));
        verify(repositoryJpa).save(any(User.class));
    }

    @Test
    void updateUserTestEmailAndName() {
        UserDto userDto = new UserDto("Пользователь 1 обновленный", "user1update@yandex.ru");
        Long id = 1L;
        Optional<User> lastUser = Optional.of(new User(1L, "Пользователь 1", "user1@yandex.ru"));
        User userUpdate = new User(id,"Пользователь 1 обновленный", "user1update@yandex.ru");
        when(repositoryJpa.existsById(anyLong())).thenReturn(true);
        when(repositoryJpa.save(any(User.class))).thenReturn(userUpdate);
        assertEquals(userUpdate, userService.updateUser(id, userDto));
        verify(repositoryJpa).save(any(User.class));
        verify(repositoryJpa, never()).findById(anyLong());
    }

    @Test
    void updateUserTestAllNull() {
        UserDto userDto = new UserDto(null, null);
        Long id = 1L;
        Optional<User> lastUser = Optional.of(new User(1L, "Пользователь 1", "user1@yandex.ru"));
        User userUpdate = new User(id,"Пользователь 1", "user1@yandex.ru");
        when(repositoryJpa.findById(anyLong())).thenReturn(lastUser);
        when(repositoryJpa.existsById(anyLong())).thenReturn(true);
        when(repositoryJpa.save(any(User.class))).thenReturn(userUpdate);
        assertEquals(userUpdate, userService.updateUser(id, userDto));
        verify(repositoryJpa).save(any(User.class));
        verify(repositoryJpa, atLeast(2)).findById(anyLong());
    }

    @Test
    void removeUserTestThrowsNotFoundDataException() {
        Long id = 1L;
        when(repositoryJpa.existsById(anyLong()))
                .thenThrow(new NotFoundDataException("Пользователя с id = " + id + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> userService.removeUser(anyLong()));
        assertEquals("Пользователя с id = " + id + " не найден", ex.getMessage());
    }

    @Test
    void getUserTestThrowsNotFoundDataException() {
        Long id = 1L;
        when(repositoryJpa.existsById(anyLong()))
                .thenThrow(new NotFoundDataException("Пользователя с id = " + id + " не найден"));
        NotFoundDataException ex = assertThrows(
                NotFoundDataException.class,
                () -> userService.getUser(id));
        assertEquals("Пользователя с id = " + id + " не найден", ex.getMessage());
    }

    @Test
    void getUserTest() {
        Long id = 1L;
        User user = new User(id,"Пользователь 1 обновленный", "user1update@yandex.ru");
        when(repositoryJpa.existsById(anyLong())).thenReturn(true);
        when(repositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUser(id));
    }

    @Test
    void getAllUserTestTwo() {
        User user1 = new User(1L,"Пользователь 1", "user1@yandex.ru");
        User user2 = new User(2L,"Пользователь 2", "user2@yandex.ru");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(repositoryJpa.findAll()).thenReturn(users);
        assertEquals(users, userService.getAllUser());
    }

    @Test
    void getAllUserTestNull() {
        List<User> users = new ArrayList<>();
        when(repositoryJpa.findAll()).thenReturn(users);
        assertEquals(users, userService.getAllUser());
    }
}