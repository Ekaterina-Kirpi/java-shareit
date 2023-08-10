package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utilits.Constants.*;

@Controller
@Slf4j
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) @Positive Long requesterId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Запрос на добавление запроса");
        return itemRequestClient.createItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(
            @RequestHeader(USER_ID_HEADER)
            @Positive Long requesterId,
            @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
            @PositiveOrZero int from,
            @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
            @Positive int size) {
        log.info("Запрос на получение запросов владельца");
        return itemRequestClient.getOwnerRequests(requesterId, from, size);
    }


    @GetMapping("all")
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(USER_ID_HEADER) @Positive Long requesterId,
            @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
            @PositiveOrZero int from,
            @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
            @Positive int size) {
        log.info("Запрос на получение запросов пользователя");
        return itemRequestClient.getUserRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Min(1) Long requestId) {
        log.info("Запрос на получение запроса {}  у пользователя {} ", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}