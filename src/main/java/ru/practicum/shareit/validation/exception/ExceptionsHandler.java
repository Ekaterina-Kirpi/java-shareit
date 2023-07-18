package ru.practicum.shareit.validation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, UserOrItemNotValidException.class,
            InvalidDataException.class, IllegalArgumentException.class})
    public ErrorResponse handleNotValidArgumentException(Exception e) {
        log.warn(e.getClass().getSimpleName(), e);
        String message;
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException valid = (MethodArgumentNotValidException) e;
            message = Objects.requireNonNull(valid.getBindingResult().getFieldError()).getDefaultMessage();
        } else {
            message = e.getMessage();
        }
        return new ErrorResponse(400, "BAD REQUEST", message);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataException.class})
    public ErrorResponse handleDataExceptionException(DataException e) {
        log.warn(e.getClass().getSimpleName(), e);
        return new ErrorResponse(409, "CONFLICT", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserOrItemNotFoundException.class, AccessException.class})
    public ErrorResponse handleDataExceptionException(RuntimeException e) {
        log.warn(e.getClass().getSimpleName(), e);
        return new ErrorResponse(404, "NOT FOUND", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ArgumentException.class})
    public ErrorResponse handleArgumentExceptionException(RuntimeException e) {
        log.warn(e.getClass().getSimpleName(), e);
        return new ErrorResponse(400, "BAD REQUEST", e.getMessage());
    }

}