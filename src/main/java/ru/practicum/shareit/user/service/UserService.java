package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto getById(long userId);

    Collection<UserDto> getAll();

    UserDto update(UserDto userDto, long userId);

    void delete(long userId);
}
