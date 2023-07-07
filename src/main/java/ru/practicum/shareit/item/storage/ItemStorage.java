package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);

    Item read(long itemId);

    Collection<Item> readByOwnerId(long userId);

    Collection<Item> searchByText(String searchText);

    Item update(Item item, long itemId);

    void delete(long itemId);
}