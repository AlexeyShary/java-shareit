package ru.practicum.shareit.user.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserInMemoryStorage implements UserStorage {
    private final HashMap<Long, User> storageMap = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        if (isEmailUsed(user.getId(), user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже занят");
        }

        user.setId(id++);
        storageMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(long userId) {
        if (!storageMap.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        return storageMap.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storageMap.values());
    }

    @Override
    public User update(UserDto userDto, long userId) {
        if (isEmailUsed(userDto.getId(), userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже занят");
        }

        User stored = storageMap.get(userId);

        stored.setName(userDto.getName());
        stored.setEmail(userDto.getEmail());

        return stored;
    }

    @Override
    public void delete(long userId) {
        storageMap.remove(userId);
    }

    private boolean isEmailUsed(Long id, String email) {
        return storageMap.values().stream()
                .filter(storedUser -> !storedUser.getId().equals(id))
                .anyMatch(storedUser -> storedUser.getEmail().equalsIgnoreCase(email));
    }
}
