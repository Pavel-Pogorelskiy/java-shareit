package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User saveUser(UserDto request);

    User updateUser(Long id, UserDto request);

    void removeUser(Long id);

    User getUser(Long id);

    List<User> getAllUser();
}
