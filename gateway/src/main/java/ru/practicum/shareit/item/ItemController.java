package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @PathVariable int itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") int userId,
                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return itemClient.getAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllBySearchText(@RequestHeader("X-Sharer-User-Id") int userId,
                                                     @RequestParam(name = "text") String text,
                                                     @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return itemClient.getAllBySearchText(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @PathVariable int itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @PathVariable int itemId,
                                         @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable int itemId) {
        itemClient.delete(itemId);
    }
}
