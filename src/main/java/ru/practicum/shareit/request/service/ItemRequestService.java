package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    ItemRequestListDto getOwnerRequests(PageRequest pageRequest, Long requesterId);

    ItemRequestListDto getUserRequests(PageRequest pageRequest, Long requesterId);

    RequestDtoResponse getItemRequestById(Long userId, Long requestId);
}
