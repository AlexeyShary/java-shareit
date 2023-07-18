package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserIdOrderByStartDesc(long userId);

    List<Booking> findAllByUserIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime endDateTime);

    List<Booking> findAllByUserIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime startDateTime);

    List<Booking> findAllByUserIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findAllByUserIdAndStatusOrderByStartDesc(long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime endDateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime startDateTime);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    List<Booking> findAllByItemId(long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.user.id = :userId AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED AND b.end < :currentTime")
    List<Booking> findAllApprovedByItemIdAndUserId(long itemId, long userId, LocalDateTime currentTime);
}
