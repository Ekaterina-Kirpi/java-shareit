package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@Slf4j
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createRequest(@RequestHeader(userIdHeader) @Min(1) Long requesterId,
                                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Запрос на добавление запроса");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.createItemRequest(itemRequestDto, requesterId));
    }

    @GetMapping
    public ResponseEntity<ItemRequestListDto> getOwnerRequests(
            @RequestHeader(userIdHeader) @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на получение запросов владельца");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService
                        .getOwnerRequests(PageRequest.of(from / size, size)
                                .withSort(Sort.by("created").descending()), requesterId));
    }

    @GetMapping("all")
    public ResponseEntity<ItemRequestListDto> getUserRequests(
            @RequestHeader(userIdHeader) @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на получение запросов пользователя");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService
                        .getUserRequests(PageRequest.of(
                                        from / size, size, Sort.by(Sort.Direction.DESC, "created")),
                                requesterId));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<RequestDtoResponse> getItemRequestById(
            @RequestHeader(userIdHeader) @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        log.info("Запрос на получение запроса " + requestId + " у пользователя " + userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.getItemRequestById(userId, requestId));
    }
}