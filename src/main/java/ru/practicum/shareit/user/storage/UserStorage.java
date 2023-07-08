package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User getById(long userId);

    List<User> getAll();

    User update(User user, long userId);

    void delete(long userId);
}
