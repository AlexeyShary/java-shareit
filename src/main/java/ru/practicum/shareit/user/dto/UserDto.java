package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Long id;
    private String name;
    @NotBlank
    @Email(message = "Некорректный e-mail")
    private String email;
}