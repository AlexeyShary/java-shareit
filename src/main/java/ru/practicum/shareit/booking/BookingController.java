package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@PathVariable int bookingId,
                                      @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingResponseDto> getAllByState(@RequestParam(required = false, defaultValue = "ALL") @Valid RequestBookingStatus state,
                                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getAllByState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByStateForOwner(@RequestParam(required = false, defaultValue = "ALL") @Valid RequestBookingStatus state,
                                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getAllByStateForOwner(state, userId);
    }

    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable int bookingId,
                                      @RequestParam boolean approved,
                                      @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.approve(bookingId, approved, userId);
    }
}