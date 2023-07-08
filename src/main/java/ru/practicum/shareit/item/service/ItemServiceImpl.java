package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = userStorage.getById(userId);
        Item item = ItemMapper.fromItemDto(itemDto);

        item.setOwner(user);

        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto getById(long itemId) {
        return ItemMapper.toItemDto(itemStorage.getById(itemId));
    }

    @Override
    public Collection<ItemDto> getByOwnerId(long userId) {
        return itemStorage.getByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getBySearchText(String searchText) {
        return searchText.isBlank() ? Collections.emptyList() :
                itemStorage.getBySearchText(searchText)
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        User user = userStorage.getById(userId);
        Item stored = itemStorage.getById(itemId);

        if (!stored.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Редактирование вещи доступно только владельцу");
        }

        itemDto.setId(itemId);
        itemDto.setName(itemDto.getName() == null ? stored.getName() : itemDto.getName());
        itemDto.setDescription(itemDto.getDescription() == null ? stored.getDescription() : itemDto.getDescription());
        itemDto.setAvailable(itemDto.getAvailable() == null ? stored.getAvailable() : itemDto.getAvailable());

        if (isValid(itemDto)) {
            return ItemMapper.toItemDto(itemStorage.update(ItemMapper.fromItemDto(itemDto), itemId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректное значение для обновления");
        }
    }

    @Override
    public void delete(long itemId) {
        itemStorage.delete(itemId);
    }

    private boolean isValid(ItemDto itemDto) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        return violations.isEmpty();
    }
}
