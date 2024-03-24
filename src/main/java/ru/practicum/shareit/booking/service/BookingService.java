package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto saveBooking(BookingResearchDto bookingDto, Long userId);

    BookingResponseDto approvedOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getBooking(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingToUser(Long userId, String state);

    List<BookingResponseDto> getBookingToOwner(Long userId, String state);
}
