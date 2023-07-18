package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.DataException;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) throws DataException {
        log.info("Запрос на добавление пользователя id: " + userDto.getId());
        return ResponseEntity.status(201).body(userService.createUser(userDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long userId, @RequestBody UserDto userDto) throws DataException {
        log.info("Запрос на обновление пользователя id: " + userId);
        return ResponseEntity.ok().body(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя id: " + userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с id: " + userId);
        return ResponseEntity.ok().body(userService.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAllUsers() throws DataException {
        log.info("Запрос на получение списка пользователей");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }
}
