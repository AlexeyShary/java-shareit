package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User read(long userId);

    Collection<User> readAll();

    User update(User user, long userId);

    void delete(long userId);
}
