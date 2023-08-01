package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;


    public UserDtoResponse createUser(UserDto user) {
        return userMapper.toUserResponseDtoFromUser(userRepository.save(userMapper.toUserFromUserDto(user)));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public UserDtoResponse updateUser(UserDtoUpdate user, Long userId) {
        User userUp = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id: " + userId + " отсутствует"));
        if (user.getName() != null) {
            userUp.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userUp.setEmail(user.getEmail());
        }
        return userMapper.toUserResponseDtoFromUser(userRepository.save(userMapper.toUserFromUserUpdateDto(user, userUp)));
    }


    public UserDtoResponse getUserById(Long userId) {
        return userMapper.toUserResponseDtoFromUser(userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id: " + userId + " отсутствует")));
    }


    public UserListDto getAllUsers() {
        return UserListDto.builder()
                .users(userRepository.findAll()
                        .stream()
                        .map(userMapper::toUserResponseDtoFromUser)
                        .collect(Collectors.toList()))
                .build();
    }


    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id: " + userId + " отсутствует");
        }
        userRepository.deleteById(userId);
    }
}