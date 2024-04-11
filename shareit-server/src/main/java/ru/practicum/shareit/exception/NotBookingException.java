package ru.practicum.shareit.exception;

public class NotBookingException extends RuntimeException {
    public NotBookingException(String message) {
        super(message);
    }
}
