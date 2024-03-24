package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto saveBooking(BookingResearchDto bookingDto, long userId);

    BookingResponseDto approvedOrRejectBooking(long userId, long bookingId, Boolean approved);

    BookingResponseDto getBooking(long userId, long bookingId);

    List<BookingResponseDto> getBookingToUser(long userId, String state);

    List<BookingResponseDto> getBookingToOwner(long userId, String state);
}
