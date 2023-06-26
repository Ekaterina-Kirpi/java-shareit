package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    public ItemDto getItem(Long id) {
        checkItems(itemStorage.getById(id));
        return itemMapper.toItemDto(itemStorage.getById(id));
    }


    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemStorage.getAll()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = itemMapper.toItem(itemDto);
        User owner = userStorage.getById(userId);
        checkUser(owner, newItem, userId);
        Item createdItem = itemStorage.create(newItem);
        return itemMapper.toItemDto(createdItem);
    }


    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = itemMapper.toItem(itemDto);
        checkUserId(userId);
        Item oldItem = itemStorage.getById(itemId);
        checkItemForUser(item, oldItem, userId);
        Item changedItem = itemStorage.update(oldItem);
        return itemMapper.toItemDto(changedItem);
    }

    public void deleteItem(Long id) {
        checkItems(itemStorage.getById(id));
        itemStorage.delete(id);
    }


    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.getAll()
                .stream()
                .filter(item ->
                        item.getName().toLowerCase().contains(text.toLowerCase())
                                || item.getDescription().toLowerCase().contains(text.toLowerCase())
                                && item.getAvailable())
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItems(Item item) {
        Item itemCheck = itemStorage.getById(item.getId());
        if (!itemStorage.getAll().contains(itemCheck)) {
            throw new NotFoundException("Вещь с id: " + itemCheck.getId() + " не найдена");
        }
        if (item.getName().isBlank()) {
            throw new NotValidException("У вещи должно быть название");
        }
        if (item.getDescription().isBlank()) {
            throw new NotValidException("Заполните описание, оно не может быть пустым");
        }
    }

    private void checkUser(User owner, Item newItem, long id) {
        if (owner == null) {
            throw new NotFoundException(String.format("Пользователь с id: " + id + " не найден"));
        }
        newItem.setOwner(owner);
    }
    

    private void checkItemForUser(Item item, Item oldItem, long userId) {

        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь не является обладателем этой вещи");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }

    private void checkUserId(Long userId) {
        if (!userStorage.getAll().contains(userStorage.getById(userId))) {
            throw new NotFoundException(String.format("Пользователь не найден"));
        }
    }
}