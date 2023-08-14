package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@Slf4j
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя");
        return userClient.createUser(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDtoUpdate userDtoUpdate, @PathVariable("id") Long userId) {
        log.info("Запрос на обновление пользователя");
        return userClient.updateUser(userDtoUpdate, userId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable("id") Long userId) {
        log.info("Запрос на удаление пользователя {}", userId);
        return userClient.deleteUser(userId);
        // return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") @Positive Long userId) {
        log.info("Запрос на получение пользователя {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Запрос на получение списка пользователей");
        return userClient.getAllUsers();
    }

}