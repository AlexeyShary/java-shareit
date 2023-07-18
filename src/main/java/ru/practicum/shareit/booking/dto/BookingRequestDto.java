package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    @NotNull(message = "Время старта не может быть пустым")
    @Future(message = "Время старта должно быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Время окончания не может быть пустым")
    @Future(message = "Время окончания должно быть в будущем")
    private LocalDateTime end;

    @AssertTrue(message = "Время окончания не может быть до старта")
    private boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }

    @AssertTrue(message = "Время старта и окончания не должны быть равны")
    private boolean isStartEqualsEnd() {
        return start == null || !start.equals(end);
    }

    @NotNull(message = "ID вещи не может быть пустым")
    private long itemId;
}