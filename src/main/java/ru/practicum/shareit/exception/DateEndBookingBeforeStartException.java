package ru.practicum.shareit.exception;

public class DateEndBookingBeforeStartException extends RuntimeException {
    public DateEndBookingBeforeStartException(String message) {
        super(message);
    }
}
