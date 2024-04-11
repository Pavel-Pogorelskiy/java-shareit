package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResearchDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid BookingResearchDto bookingDto) {
        log.info("Сохранение нового запроса на бронирование {}", bookingDto);
        return bookingClient.saveBooking(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> approvedOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @PathVariable Long bookingId,
                                                          @RequestParam boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование c id = {}", bookingId);
        return bookingClient.approvedOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получение запроса на бронирование с id = ", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsToBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @Min(0) Long from,
                                                        @RequestParam(required = false) @Min(1) Long size) {
        if (size == null) {
            log.info("Получение запросов на бронирование для пользователя с id = {}", userId);
            return bookingClient.getBookings(userId, state, from, Long.MAX_VALUE);
        } else {
            log.info("Получение запросов на бронирование для пользователя с id = {}", userId);
            return bookingClient.getBookings(userId, state, from, size);
        }
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getBookingsToOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @Min(0) Long from,
                                                     @RequestParam(required = false) @Min(1) Long size) {
        if (size == null) {
            log.info("Получение запросов на бронирование для пользователя с id = {}", userId);
            return bookingClient.getBookingsOwner(userId, state, from, Long.MAX_VALUE);
        } else {
            log.info("Получение запросов на бронирование для пользователя с id = {}", userId);
            return bookingClient.getBookingsOwner(userId, state, from, size);
        }
    }
}
