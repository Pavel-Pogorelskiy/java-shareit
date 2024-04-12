package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid BookingResearchDto bookingDto) {
        log.info("Сохранение нового запроса на бронирование {}", bookingDto);
        return bookingService.saveBooking(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingResponseDto approvedOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование c id = {}", bookingId);
        return bookingService.approvedOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Получение запроса на бронирование с id = {} ", bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsToBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @Min(0) Long from,
                                                        @RequestParam(required = false) @Min(1) Long size) {
        if (size == null) {
            size = Long.MAX_VALUE;
        }
            log.info("Получение запросов на бронирование для пользователя с id = {}, " +
                    "state = {}, from = {}, size = {}", userId, state, from, size);
            return bookingService.getBookingToUser(userId, state, from, size);
        }

    @GetMapping(value = "/owner")
    public List<BookingResponseDto> getBookingsToOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") @Min(0) Long from,
                                                       @RequestParam(required = false) @Min(1) Long size) {
        if (size == null) {
            size = Long.MAX_VALUE;
        }
            log.info("Получение запросов на бронирование других пользователей для пользователя с id = {}, " +
                    "state = {}, from = {}, size = {}", userId, state, from, size);
            return bookingService.getBookingToOwner(userId, state, from, size);
    }
}
