package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExceptionsHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> NotFoundExceptionHandler(NotFoundException exception) {
        log.error("Объект не найден :( ", exception);
        return Map.of(
                "ERROR", "Объект не найден :( ",
                "errorMessage", exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> NotValidExceptionHandler(NotValidException exception) {
        log.error("Неверно введены данные ", exception);
        return Map.of(
                "ERROR", "Неверно введены данные ",
                "errorMessage", exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> EmailExceptionHandler(EmailException exception) {
        log.error(exception.getMessage());
        return Map.of("ERROR", exception.getMessage());
    }


}