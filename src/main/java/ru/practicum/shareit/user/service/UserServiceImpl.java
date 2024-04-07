package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.response.UserRepositoryJpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepositoryJpa userRepository;

    @Override
    @Transactional
    public User saveUser(UserDto request) {
        User user = userMapper.toUser(request);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDto request) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundDataException("Пользователя с id = " + id + " не найден");
        }
        User user = userMapper.toUser(request);
        user.setId(id);
        if (user.getEmail() == null) {
            user.setEmail(userRepository.findById(id).get().getEmail());
        }
        if (user.getName() == null) {
            user.setName(userRepository.findById(id).get().getName());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundDataException("Пользователя с id = " + id + " не найден");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundDataException("Пользователя с id = " + id + " не найден");
        }
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
