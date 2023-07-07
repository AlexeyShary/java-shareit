package ru.practicum.shareit.user.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
public class UserInMemoryStorage implements UserStorage {
    private final HashMap<Long, User> storageMap = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        if (isEmailUsed(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже занят");
        }

        user.setId(id++);
        storageMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User read(long userId) {
        if (!storageMap.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        return storageMap.get(userId);
    }

    @Override
    public Collection<User> readAll() {
        return storageMap.values();
    }

    @Override
    public User update(User user, long userId) {
        if (isEmailUsed(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже занят");
        }

        User stored = storageMap.get(userId);

        stored.setName(user.getName());
        stored.setEmail(user.getEmail());

        return stored;
    }

    @Override
    public void delete(long userId) {
        storageMap.remove(userId);
    }

    private boolean isEmailUsed(User user) {
        return storageMap.values().stream()
                .filter(storedUser -> !storedUser.getId().equals(user.getId()))
                .anyMatch(storedUser -> storedUser.getEmail().equalsIgnoreCase(user.getEmail()));
    }
}
