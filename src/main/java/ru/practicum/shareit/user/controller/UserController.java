package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Controller
@Slf4j
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userDto));
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserDtoResponse> updateUser(@RequestBody UserDtoUpdate userDtoUpdate, @PathVariable("id") Long userId) {
        log.info("Запрос на обновление пользователя");
        return ResponseEntity.ok()
                .body(userService.updateUser(userDtoUpdate, userId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@Min(1) @PathVariable("id") Long userId) {
        log.info("Запрос на удаление пользователя {}",userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDtoResponse> getUserById(@PathVariable("id") @Min(1) Long userId) {
        log.info("Запрос на получение пользователя {}",userId);
        return ResponseEntity.ok()
                .body(userService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<UserListDto> getUsers() {
        log.info("Запрос на получение списка пользователей");
        return ResponseEntity.ok()
                .body(userService.getAllUsers());
    }

}