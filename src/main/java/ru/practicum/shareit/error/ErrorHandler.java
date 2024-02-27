package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.AnotherUserException;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundDataException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundDataException(final NotFoundDataException exception) {
        log.info("{}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerDuplicateEmail(final DuplicateEmailException exception) {
        log.info("{}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handlerAnotherUser(final AnotherUserException exception) {
        log.info("{}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerMissingRequestHeader(final MissingRequestHeaderException exception) {
        log.info("{}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}
