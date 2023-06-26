package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        checkEmail(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        checkUserId(user.getId());
        checkEmail(user);
        User userUp = users.get(user.getId());
        if (user.getName() != null && !user.getName().isEmpty()) {
            userUp.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            userUp.setEmail(user.getEmail());
        }
        return userUp;
    }


    @Override
    public User getById(Long userId) {
        checkUserId(userId);
        return users.get(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Boolean delete(Long userId) {
        return users.remove(userId) != null;

    }

    public void checkUserId(Long id) {
        if (id != 0 && !users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");

        }
    }

    public void checkEmail(User user) {
        if (users.values().stream()
                .anyMatch(
                        stored -> stored.getEmail().equalsIgnoreCase(user.getEmail())
                                && stored.getId() != user.getId())) {
            throw new EmailException("Пользователь с таким mail " +
                    user.getEmail() + " уже зарегистрирован ");
        }

    }
}
