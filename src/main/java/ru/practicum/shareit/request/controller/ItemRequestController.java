package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.utilits.Constants.USER_ID_HEADER;

@Controller
@Slf4j
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestServiceImpl itemRequestServiceImpl;

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createRequest(@RequestHeader(USER_ID_HEADER) @Min(1) Long requesterId,
                                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Запрос на добавление запроса");
        return ResponseEntity.ok()
                .body(itemRequestServiceImpl.createItemRequest(itemRequestDto, requesterId));
    }

    @GetMapping
    public ResponseEntity<ItemRequestListDto> getOwnerRequests(
            @RequestHeader(USER_ID_HEADER) @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на получение запросов владельца");
        return ResponseEntity.ok()
                .body(itemRequestServiceImpl
                        .getOwnerRequests(PageRequest.of(from / size, size)
                                .withSort(Sort.by("created").descending()), requesterId));
    }

    @GetMapping("all")
    public ResponseEntity<ItemRequestListDto> getUserRequests(
            @RequestHeader(USER_ID_HEADER) @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на получение запросов пользователя");
        return ResponseEntity.ok()
                .body(itemRequestServiceImpl
                        .getUserRequests(PageRequest.of(
                                        from / size, size, Sort.by(Sort.Direction.DESC, "created")),
                                requesterId));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<RequestDtoResponse> getItemRequestById(
            @RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        log.info("Запрос на получение запроса {}  у пользователя {} ", requestId, userId);
        return ResponseEntity.ok()
                .body(itemRequestServiceImpl.getItemRequestById(userId, requestId));
    }
}