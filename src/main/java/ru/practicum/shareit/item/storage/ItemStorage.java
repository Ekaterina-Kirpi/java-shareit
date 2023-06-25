package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

//Dto
public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item getById(Long id);

    List<Item> getAll();

    void delete(Long id);
}