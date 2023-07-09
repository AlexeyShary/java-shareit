package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.create(UserMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userStorage.getById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {
        User stored = userStorage.getById(userId);

        userDto.setId(userId);
        userDto.setName(userDto.getName() == null ? stored.getName() : userDto.getName());
        userDto.setEmail(userDto.getEmail() == null ? stored.getEmail() : userDto.getEmail());

        if (isValid(userDto)) {
            return UserMapper.toUserDto(userStorage.update(userDto, userId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректное значение для обновления");
        }
    }

    @Override
    public void delete(long userId) {
        userStorage.delete(userId);
    }

    private boolean isValid(UserDto userDto) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        return violations.isEmpty();
    }
}