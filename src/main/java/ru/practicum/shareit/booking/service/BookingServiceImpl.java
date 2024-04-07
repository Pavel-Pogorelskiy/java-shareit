package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.response.BookingRepositoryJpa;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepositoryJpa bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto saveBooking(BookingResearchDto bookingDto, long userId) {
        User booker = userService.getUser(userId);
        Item item = itemService.getItemToBooking(bookingDto.getItemId());
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new DateEndBookingBeforeStartException("Дата конца бронирования раньше или равна дате начала");
        }
        if (!item.getAvailable()) {
            throw new UnavailableItemException("Предмет с id = " + item.getId() + " недоступен");
        }
        if (item.getOwner().getId().longValue() == booker.getId()) {
            throw new UserNotAccessException("Пользователь с id = " + booker.getId() + " является владельцем вещи " +
                    "с id = " + item.getId());
        }
        Booking research = bookingMapper.toBooking(bookingDto);
        research.setStatus(Status.WAITING);
        research.setItem(item);
        research.setBooker(booker);
        return bookingMapper.toDto(bookingRepository.save(research));
    }

    @Override
    @Transactional
    public BookingResponseDto approvedOrRejectBooking(long userId, long bookingId, Boolean approved) {
        User owner = userService.getUser(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundDataException("Запрос на бронирование с id = " + bookingId + " не найден");
        }
        if (booking.get().getItem().getOwner().getId().longValue() != owner.getId()) {
            throw new UserNotAccessException("Пользователь с id = " + owner.getId() + " не является владельцем вещи" +
                    " с id = " + booking.get().getItem().getId());
        }
        if (booking.get().getStatus() != Status.WAITING) {
            throw new NotAccessChangeStatus("Нет доступа к изменению статуса");
        }
        if (approved) {
            booking.get().setStatus(Status.APPROVED);
        } else {
            booking.get().setStatus(Status.REJECTED);
        }
        return bookingMapper.toDto(bookingRepository.save(booking.get()));
    }

    @Override
    public BookingResponseDto getBooking(long userId, long bookingId) {
        User user = userService.getUser(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundDataException("Запрос на бронирование с id = " + bookingId + " не найден");
        }
        if (booking.get().getItem().getOwner().getId().longValue() != user.getId()
                && user.getId().longValue() != booking.get().getBooker().getId()) {
            throw new UserNotAccessException("Пользователь с id = " + user.getId() + " не является владельцем вещи" +
                    " с id = " + booking.get().getItem().getId() + " или владельцем запроса на бронирования id = "
                    + booking.get().getId());
        }
        return bookingMapper.toDto(booking.get());
    }

    @Override
    public List<BookingResponseDto> getBookingToUser(long userId, String state, long from, long size) {
        userService.getUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now())
                                && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("PAST"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("WAITING"):
                bookings = bookingRepository.findBookingByBooker_IdAndStatus(userId, Status.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findBookingByBooker_IdAndStatus(userId, Status.REJECTED);
                break;
            default:
                throw new NotFoundStateException("Unknown state: " + state);
        }
        return bookingMapper.toListDto(bookings).stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingToOwner(long userId, String state, long from, long size) {
        userService.getUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now())
                                && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("PAST"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("WAITING"):
                bookings = bookingRepository.findBookingByItem_Owner_IdAndStatus(userId, Status.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findBookingByItem_Owner_IdAndStatus(userId, Status.REJECTED);
                break;
            default:
                throw new NotFoundStateException("Unknown state: " + state);
        }
        return bookingMapper.toListDto(bookings).stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }
}
