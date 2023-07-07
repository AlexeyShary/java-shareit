package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@PathVariable long itemId) {
        return itemService.read(itemId);
    }

    @GetMapping
    public Collection<ItemDto> readByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.readByOwnerId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByText(@RequestParam(name = "text") String searchText) {
        return itemService.searchByText(searchText);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable long itemId) {
        itemService.delete(itemId);
    }
}
