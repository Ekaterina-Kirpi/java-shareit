package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(userIdHeader) Long userId) {
        log.info("Запрос на добавление вещи id: " + itemDto.getId() + " у пользователя id: " + userId);
        return ResponseEntity.status(201).body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(userIdHeader) Long userId, @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи id: " + itemId + "у пользователа id: " + userId);
        return ResponseEntity.ok().body(itemService.updateItem(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
        itemService.deleteItem(itemId, userId);
        log.info("Запрос на удаление вещи id: " + itemId + " у пользователя id: " + userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
        log.info("Запрос на получение вещи id: " + itemId + " у пользователя id: " + userId);
        return ResponseEntity.ok().body(itemService.getItem(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader(userIdHeader) Long userId) {
        List<ItemDto> items = itemService.getAllItemsByUserId(userId);
        log.info("Запрос на получение списка вещей у пользователя id: " + userId);
        return ResponseEntity.ok().body(itemService.getAllItemsByUserId(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(@RequestParam(name = "text") String text) {
        log.info("Запрос на поиск вещи: " + text);
        return ResponseEntity.ok().body(itemService.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId,
                                                 @RequestBody @Valid CommentDto commentDto) {
        log.info("Запрос на добавление комментария для вещи id: " + itemId + "пользователем id: " + userId);
        return ResponseEntity.ok().body(itemService.createComment(userId, itemId, commentDto));
    }
}
