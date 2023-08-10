package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.dto.ItemListDto;

public interface ItemService {
    ItemDtoResponse createItem(ItemDto item, Long userId);


    ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item);

    ItemDtoResponse getItemById(Long userId, Long itemId);

    ItemListDto getAllItemsOwner(Long userId, int from, int size);

    ItemListDto search(String text, int from, int size);
}
