package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<User> getAllUser() {
        log.info("Получение всех пользователей: {}", userService.getAllUser());
        return userService.getAllUser();
    }

    @GetMapping(value = "/{userId}")
    public User getUser(@PathVariable Long userId) {
        log.info("Получение пользователя с id = " + userId + ": {}", userService.getUser(userId));
        return userService.getUser(userId);
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody UserDto request) {
        log.info("Сохранение пользователя {}", request);
        return userService.saveUser(request);
    }

    @PatchMapping(value = "/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody UserDto request) {
        log.info("Обновление пользователя {}", request);
        return userService.updateUser(userId, request);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id ={}", userId);
        userService.removeUser(userId);
    }
}