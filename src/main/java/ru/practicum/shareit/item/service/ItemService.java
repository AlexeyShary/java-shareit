package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto read(long itemId);

    Collection<ItemDto> readByOwnerId(long userId);

    Collection<ItemDto> searchByText(String searchText);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    void delete(long itemId);
}
