package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item getById(long itemId);

    List<Item> getByOwnerId(long userId);

    List<Item> getBySearchText(String searchText);

    Item update(Item item, long itemId);

    void delete(long itemId);
}