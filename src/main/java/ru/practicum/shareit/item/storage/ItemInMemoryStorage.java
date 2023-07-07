package ru.practicum.shareit.item.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryStorage implements ItemStorage {
    private final HashMap<Long, Item> storageMap = new HashMap<>();
    private long id = 1;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        storageMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item read(long itemId) {
        if (!storageMap.containsKey(itemId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена");
        }

        return storageMap.get(itemId);
    }

    @Override
    public Collection<Item> readByOwnerId(long userId) {
        return storageMap.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    public Collection<Item> searchByText(String searchText) {
        return storageMap.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText.toLowerCase())
                        || item.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item, long itemId) {
        Item stored = storageMap.get(itemId);

        stored.setName(item.getName());
        stored.setDescription(item.getDescription());
        stored.setAvailable(item.getAvailable());

        return stored;
    }

    @Override
    public void delete(long itemId) {
        storageMap.remove(itemId);
    }
}
