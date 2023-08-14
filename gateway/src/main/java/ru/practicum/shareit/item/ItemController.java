package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utilits.Constants.USER_ID_HEADER;

@Controller
@Slf4j
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи {} у пользователя {} ", itemDto.getName(), userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                             @RequestBody ItemDtoUpdate itemDtoUpdate,
                                             @PathVariable @Positive Long itemId) {
        log.info("Запрос на обновление вещи {} у пользователя {} ", itemId, userId);
        return itemClient.updateItem(itemId, userId, itemDtoUpdate);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                              @PathVariable @Positive Long itemId) {
        log.info("Запрос на получение вещи {} у пользователя {} ", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение списка вещей у пользователя {}", userId);
        return itemClient.getAllItemsOwner(userId, from, size);
    }

    @GetMapping("search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на поиск вещи: {}", text);
        return itemClient.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable @Positive Long itemId,
                                             @RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария для вещи {} пользователем {}", itemId, userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }

}