package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;

public interface UserService {
    UserDtoResponse createUser(UserDto user);
    UserDtoResponse updateUser(UserDtoUpdate user, Long userId);
    UserDtoResponse getUserById(Long userId);
    UserListDto getAllUsers();
    void deleteUser(Long userId);

}
