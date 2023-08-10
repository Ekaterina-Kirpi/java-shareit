package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice("ru.practicum.shareit")
public class ExceptionsHandler {

    @ExceptionHandler(ResponseStatusException.class)
    private ResponseEntity<String> handleException(ResponseStatusException exception) {
        log.debug("Получен статус {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<String> handleException(MethodArgumentNotValidException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HttpStatus.BAD_REQUEST + " " + exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<String> handleException() {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR - нарушение уникального индекса или первичного ключа");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HttpStatus.INTERNAL_SERVER_ERROR + " Нарушение уникального индекса или первичного ключа");
    }

    @ExceptionHandler(StateException.class)
    private ResponseEntity<StateErrorResponse> handleException(StateException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<String> handleException(ConstraintViolationException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HttpStatus.BAD_REQUEST + " " + exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    private ResponseEntity<String> handleException(Throwable exception) {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HttpStatus.INTERNAL_SERVER_ERROR + exception.getMessage());
    }
}