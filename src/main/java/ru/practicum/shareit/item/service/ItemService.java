package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto getById(long itemId);

    List<ItemDto> getByOwnerId(long userId);

    List<ItemDto> getBySearchText(String searchText);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    void delete(long itemId);
}
