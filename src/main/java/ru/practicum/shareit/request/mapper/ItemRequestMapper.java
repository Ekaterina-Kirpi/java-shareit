package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest toItemRequestFromItemRequestDto(ItemRequestDto itemRequestDto);

    ItemRequestDtoResponse toItemRequestResponseDtoFromItemRequest(ItemRequest itemRequest);

    @Mapping(source = "request.id", target = "requestId")
    ItemForRequestDto toItemForRequestDtoFromItem(Item item);

    RequestDtoResponse toListRequestDtoToResponseFromListItemRequest(ItemRequest itemRequest);

    List<RequestDtoResponse> toListRequestDtoToResponseFromListItemRequest(List<ItemRequest> itemRequests);


}