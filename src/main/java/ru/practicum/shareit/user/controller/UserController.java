package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;


@Controller
@Slf4j
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userServiceImpl.createUser(userDto));
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserDtoResponse> updateUser(@RequestBody UserDtoUpdate userDtoUpdate, @PathVariable("id") Long userId) {
        log.info("Запрос на обновление пользователя");
        return ResponseEntity.ok()
                .body(userServiceImpl.updateUser(userDtoUpdate, userId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable("id") Long userId) {
        log.info("Запрос на удаление пользователя {}", userId);
        userServiceImpl.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDtoResponse> getUserById(@PathVariable("id") @Positive Long userId) {
        log.info("Запрос на получение пользователя {}", userId);
        return ResponseEntity.ok()
                .body(userServiceImpl.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<UserListDto> getUsers() {
        log.info("Запрос на получение списка пользователей");
        return ResponseEntity.ok()
                .body(userServiceImpl.getAllUsers());
    }

}