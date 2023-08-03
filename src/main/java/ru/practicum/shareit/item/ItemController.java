package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable int itemId,
                                    @RequestHeader("X-Sharer-User-Id") int userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(commentDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") int userId,
                           @PathVariable int itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                         @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return itemService.getAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllBySearchText(@RequestParam(name = "text") String searchText,
                                            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                            @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return itemService.getAllBySearchText(searchText, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable int itemId,
                          @RequestHeader("X-Sharer-User-Id") int userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable int itemId) {
        itemService.delete(itemId);
    }
}
