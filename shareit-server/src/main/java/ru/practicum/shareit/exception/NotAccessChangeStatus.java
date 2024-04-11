package ru.practicum.shareit.exception;

public class NotAccessChangeStatus extends RuntimeException {
    public NotAccessChangeStatus(String message) {
        super(message);
    }
}
