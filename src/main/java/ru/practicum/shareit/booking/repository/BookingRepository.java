package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByUserIdOrderByStartDesc(int userId);

    List<Booking> findAllByUserIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime endDateTime);

    List<Booking> findAllByUserIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime startDateTime);

    List<Booking> findAllByUserIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findAllByUserIdAndStatusOrderByStartDesc(int userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(int ownerId, LocalDateTime endDateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(int ownerId, LocalDateTime startDateTime);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int ownerId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(int ownerId, BookingStatus status);

    List<Booking> findAllByItemId(int itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.user.id = :userId AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED AND b.end < :currentTime")
    List<Booking> findAllApprovedByItemIdAndUserId(int itemId, int userId, LocalDateTime currentTime);
}
