package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User getById(Long userId);

    Collection<User> getAll();

    Boolean delete(Long userId);

}
