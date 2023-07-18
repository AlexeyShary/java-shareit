package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    CommentDto createComment(CommentDto commentDto, long userId, long itemId);

    ItemDto getById(long userId, long itemId);

    List<ItemDto> getAllByOwnerId(long userId);

    List<ItemDto> getAllBySearchText(String searchText);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    void delete(long itemId);
}
