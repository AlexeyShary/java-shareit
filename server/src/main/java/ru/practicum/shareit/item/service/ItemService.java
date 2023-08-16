package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, int userId);

    CommentDto createComment(CommentDto commentDto, int userId, int itemId);

    ItemDto getById(int userId, int itemId);

    List<ItemDto> getAllByOwnerId(int userId, int from, int size);

    List<ItemDto> getAllBySearchText(String searchText, int from, int size);

    ItemDto update(ItemDto itemDto, int itemId, int userId);

    void delete(int itemId);
}
