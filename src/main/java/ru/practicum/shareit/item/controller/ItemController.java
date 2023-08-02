package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.utilits.Constants.USER_ID_HEADER;

@Controller
@Slf4j
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDtoResponse> createItem(@RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
                                                      @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи {} у пользователя {} ", itemDto.getName(), userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
                                                      @RequestBody ItemDtoUpdate itemDtoUpdate,
                                                      @PathVariable @Min(1) Long itemId) {
        log.info("Запрос на обновление вещи {} у пользователя {} ", itemId, userId);
        return ResponseEntity.ok()
                .body(itemService.updateItem(itemId, userId, itemDtoUpdate));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> getItemById(@RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
                                                       @PathVariable @Min(1) Long itemId) {
        log.info("Запрос на получение вещи {} у пользователя {} ", itemId, userId);
        return ResponseEntity.ok()
                .body(itemService.getItemById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<ItemListDto> getAllItems(
            @RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на получение списка вещей у пользователя {}", userId);
        return ResponseEntity.ok()
                .body(itemService.getAllItemsOwner(PageRequest.of(from / size, size), userId));
    }

    @GetMapping("search")
    public ResponseEntity<ItemListDto> searchItems(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на поиск вещи: {}", text);
        return ResponseEntity.ok()
                .body(itemService.search(PageRequest.of(from / size, size), text));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> addComment(@PathVariable @Min(1) Long itemId,
                                                         @RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
                                                         @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария для вещи {} пользователем {}", itemId, userId);
        return ResponseEntity.ok()
                .body(itemService.createComment(itemId, userId, commentDto));
    }

}