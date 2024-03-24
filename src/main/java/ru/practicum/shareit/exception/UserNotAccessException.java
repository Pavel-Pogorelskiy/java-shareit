package ru.practicum.shareit.exception;

public class UserNotAccessException extends RuntimeException {
    public UserNotAccessException(String message) {
        super(message);
    }
}
