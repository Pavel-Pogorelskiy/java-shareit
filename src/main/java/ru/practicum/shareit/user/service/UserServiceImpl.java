package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.response.UserResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserResponse userResponse;
    private Long idUser = 0L;

    @Override
    public User saveUser(UserDto request) {
        User user = userMapper.toUser(request);
        userResponse.validDupEmail(user);
        user.setId(idUser());
        return userResponse.save(user);
    }

    @Override
    public User uptadeUser(Long id, UserDto request) {
        return userResponse.uptade(id, userMapper.toUser(request));
    }

    @Override
    public void removeUser(Long id) {
        userResponse.remove(id);
    }

    @Override
    public User getUser(Long id) {
        return userResponse.get(id);
    }

    @Override
    public List<User> getAllUser() {
        return userResponse.getAll();
    }

    private Long idUser() {
        return ++idUser;
    }
}
