package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponseDto getById(long bookingId, long userId);

    List<BookingResponseDto> getAllByState(RequestBookingStatus requestBookingStatus, long userId);

    List<BookingResponseDto> getAllByStateForOwner(RequestBookingStatus requestBookingStatus, long userId);

    BookingResponseDto create(BookingRequestDto bookingRequestDto, long userId);

    BookingResponseDto approve(long bookingId, boolean approved, long userId);
}
