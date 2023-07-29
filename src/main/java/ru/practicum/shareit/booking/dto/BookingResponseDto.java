package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookingResponseUserDto booker;
    private BookingResponseItemDto item;

    @Data
    @Builder
    public static class BookingResponseUserDto {
        private Integer id;
    }

    @Data
    @Builder
    public static class BookingResponseItemDto {
        private Integer id;
        private String name;
    }
}