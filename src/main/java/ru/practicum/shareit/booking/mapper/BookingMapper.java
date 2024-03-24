package ru.practicum.shareit.booking.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookingMapper.class)

public interface BookingMapper {
    Booking toBooking(BookingResearchDto bookingResearchDto);

    BookingResponseDto toDto(Booking booking);

    BookerDto toBookerDto(User booker);

    ItemBookingResponseDto toItemDto(Item item);

    List<BookingResponseDto> toListDto(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingResponseToItemDto toBookingToItem(Booking booking);

}
