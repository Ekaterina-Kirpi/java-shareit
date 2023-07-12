package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserStorage userStorage;

    public UserDto getUser(Long id) {
        return userMapper.toUserDto(userStorage.getById(id));
    }


    public Collection<UserDto> getAllUsers() {
        return userStorage.getAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }


    public UserDto createUser(UserDto userDto) {
        User user = userStorage.create(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }


    public UserDto updateUser(UserDto userDto, Long id) {
        userDto.setId(id);
        return userMapper.toUserDto(userStorage.update(userMapper.toUser(userDto)));
    }

    public Boolean deleteUser(Long id) {
        return userStorage.delete(id);
    }
}