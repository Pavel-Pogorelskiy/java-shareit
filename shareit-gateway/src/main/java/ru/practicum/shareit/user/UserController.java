package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAllUser() {
        log.info("Получение всех пользователей");
        return userClient.getAllUser();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Получение пользователя с id = " + userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserDto request) {
        log.info("Сохранение пользователя {}", request);
        return userClient.saveUser(request);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto request) {
        log.info("Обновление пользователя {}", request);
        return userClient.updateUser(userId, request);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id ={}", userId);
        userClient.removeUser(userId);
    }
}