package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utilits.Constants.USER_ID_HEADER;

@Controller
@Slf4j
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;

    @PostMapping
    public ResponseEntity<ItemDtoResponse> createItem(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                      @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи {} у пользователя {} ", itemDto.getName(), userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemServiceImpl.createItem(itemDto, userId));
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                      @RequestBody ItemDtoUpdate itemDtoUpdate,
                                                      @PathVariable @Positive Long itemId) {
        log.info("Запрос на обновление вещи {} у пользователя {} ", itemId, userId);
        return ResponseEntity.ok()
                .body(itemServiceImpl.updateItem(itemId, userId, itemDtoUpdate));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> getItemById(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                       @PathVariable @Positive Long itemId) {
        log.info("Запрос на получение вещи {} у пользователя {} ", itemId, userId);
        return ResponseEntity.ok()
                .body(itemServiceImpl.getItemById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<ItemListDto> getAllItems(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение списка вещей у пользователя {}", userId);
        return ResponseEntity.ok()
                .body(itemServiceImpl.getAllItemsOwner(userId, from, size));
    }

    @GetMapping("search")
    public ResponseEntity<ItemListDto> searchItems(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на поиск вещи: {}", text);
        return ResponseEntity.ok()
                .body(itemServiceImpl.search(text, from, size));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> addComment(@PathVariable @Min(1) Long itemId,
                                                         @RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                         @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария для вещи {} пользователем {}", itemId, userId);
        return ResponseEntity.ok()
                .body(itemServiceImpl.createComment(itemId, userId, commentDto));
    }

}