package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByUserIdOrderByStartDesc(int userId, Pageable pageable);

    Page<Booking> findAllByUserIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime endDateTime, Pageable pageable);

    Page<Booking> findAllByUserIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime startDateTime, Pageable pageable);

    Page<Booking> findAllByUserIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    Page<Booking> findAllByUserIdAndStatusOrderByStartDesc(int userId, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(int ownerId, LocalDateTime endDateTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(int ownerId, LocalDateTime startDateTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int ownerId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(int ownerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemId(int itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.user.id = :userId AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED AND b.end < :currentTime")
    List<Booking> findAllApprovedByItemIdAndUserId(int itemId, int userId, LocalDateTime currentTime);
}
