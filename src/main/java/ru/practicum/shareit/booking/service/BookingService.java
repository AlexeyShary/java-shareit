package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponseDto getById(int bookingId, int userId);

    List<BookingResponseDto> getAllByState(RequestBookingStatus requestBookingStatus, int userId, int from, int size);

    List<BookingResponseDto> getAllByStateForOwner(RequestBookingStatus requestBookingStatus, int userId, int from, int size);

    BookingResponseDto create(BookingRequestDto bookingRequestDto, int userId);

    BookingResponseDto approve(int bookingId, boolean approved, int userId);
}
