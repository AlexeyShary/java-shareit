package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);

    Item getById(long itemId);

    Collection<Item> getByOwnerId(long userId);

    Collection<Item> getBySearchText(String searchText);

    Item update(Item item, long itemId);

    void delete(long itemId);
}