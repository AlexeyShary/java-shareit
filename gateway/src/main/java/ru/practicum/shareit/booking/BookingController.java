package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @PathVariable int bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByState(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @RequestParam(required = false, defaultValue = "ALL") @Valid RequestBookingStatus state,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return bookingClient.getAllByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByStateForOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                                        @RequestParam(required = false, defaultValue = "ALL") @Valid RequestBookingStatus state,
                                                        @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                        @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return bookingClient.getAllByStateForOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingClient.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @PathVariable int bookingId,
                                          @RequestParam boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }
}