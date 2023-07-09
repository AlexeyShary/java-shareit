package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User getById(long userId);

    List<User> getAll();

    User update(UserDto userDto, long userId);

    void delete(long userId);
}
