package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.exception.DataException;
import ru.practicum.shareit.validation.exception.UserOrItemNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.hibernate.cfg.AvailableSettings.USER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userFromDto(userDto);
        User userSaved = userRepository.save(user);
        return userMapper.userToDto(userSaved);
    }


    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userMapper.userFromDto(userDto);
        User userUp = userRepository.findById(id).orElseThrow(() -> new DataException(USER, id));
        if (StringUtils.hasLength(user.getEmail())) {
            userUp.setEmail(user.getEmail());
        }
        if (StringUtils.hasLength(user.getName())) {
            userUp.setName(user.getName());
        }
        User userSave = userRepository.save(userUp);


        return userMapper.userToDto(userSave);
    }

    @Transactional(readOnly = true)
    //Возвращает Пользователя User по идентификатору
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserOrItemNotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    @Transactional(readOnly = true)
    //Возвращает Пользователя UserDto по идентификатору
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserOrItemNotFoundException("Пользователь с id: " + userId + " не найден"));
        return userMapper.userToDto(user);
    }

    @Transactional(readOnly = true)
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}