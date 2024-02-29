package ru.practicum.shareit.user.response;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserResponse {
    User save(User user);

    User update(Long id, User user);

    void remove(Long id);

    User get(Long id);

    void validDupEmail(User user);

    List<User> getAll();
}
