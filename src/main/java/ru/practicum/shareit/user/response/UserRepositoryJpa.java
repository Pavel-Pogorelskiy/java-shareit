package ru.practicum.shareit.user.response;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepositoryJpa extends JpaRepository<User, Long> {
}
