package ru.practicum.shareit.booking.response;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b where b.booker.id = :bookerId and b.status = :status order by b.start desc")
    List<Booking> findBookingByBooker_IdAndStatus(Long bookerId, Status status);

    List<Booking> findBookingByBooker_IdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking as b where b.item.owner.id = :ownerId and b.status = :status order by b.start desc")
    List<Booking> findBookingByItem_Owner_IdAndStatus(Long ownerId, Status status);

    List<Booking> findBookingByItem_Owner_IdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking as b where b.item.id = :itemId and b.start > CURRENT_TIMESTAMP " +
            "and b.status = :status order by b.start asc")
    List<Booking> findBookingByItemAndStartAfter(Long itemId, Status status);

    @Query("select b from Booking as b where b.item.id = :itemId and b.start < CURRENT_TIMESTAMP " +
            "and b.status = :status order by b.start desc")
    List<Booking> findBookingByItemAndStartBefore(Long itemId, Status status);

    List<Booking> findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(Long itemId, Long bookerId,
                                                                    LocalDateTime end, Status status);
}
