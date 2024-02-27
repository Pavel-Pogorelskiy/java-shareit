package ru.practicum.shareit.user.response;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserResponseImpl implements UserResponse {
    private Map<Long, User> usersStorage = new HashMap<>();

    @Override
    public User save(User user) {
        usersStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User uptade(Long id, User user) {
        User oldUser = usersStorage.get(id);
        if (user.getEmail() != null) {
            if (!user.getEmail().contains(oldUser.getEmail())) {
                validDupEmail(user);
            }
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        usersStorage.put(id, oldUser);
        return oldUser;
    }

    @Override
    public void remove(Long id) {
        usersStorage.remove(id);
    }

    @Override
    public User get(Long id) {
        return usersStorage.get(id);
    }



    @Override
    public void validDupEmail(User user) {
        List<User> dupUserEmail = usersStorage.values().stream()
                .filter(user1 -> user1.getEmail().contains(user.getEmail()))
                .collect(Collectors.toList());
        if (dupUserEmail.size() > 0) {
            throw new DuplicateEmailException("Пользователь с email " + user.getEmail() + " уже существует");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(usersStorage.values());
    }
}
