package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
    @NotNull(message = "Доступность должна быть явно указана")
    private Boolean available;

    private ItemBooking nextBooking;
    private ItemBooking lastBooking;

    private List<CommentDto> comments;

    private Integer requestId;

    @Data
    @Builder
    public static class ItemBooking {
        private Integer id;
        private Integer bookerId;
    }
}
