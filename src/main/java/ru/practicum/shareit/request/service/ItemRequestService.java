package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    ItemRequestListDto getOwnerRequests(Long requesterId, int from, int size);

    ItemRequestListDto getUserRequests(Long requesterId, int from, int size);

    RequestDtoResponse getItemRequestById(Long userId, Long requestId);
}
