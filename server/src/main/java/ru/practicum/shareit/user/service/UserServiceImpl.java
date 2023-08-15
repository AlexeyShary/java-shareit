package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        try {
            return UserMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(int userId) {
        return UserMapper.toDto(ServiceUtil.getUserOrThrowNotFound(userId, userRepository));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, int userId) {
        User stored = ServiceUtil.getUserOrThrowNotFound(userId, userRepository);

        Optional.ofNullable(userDto.getName()).ifPresent(stored::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(stored::setEmail);

        try {
            return UserMapper.toDto(userRepository.save(stored));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(int userId) {
        userRepository.deleteById(userId);
    }
}