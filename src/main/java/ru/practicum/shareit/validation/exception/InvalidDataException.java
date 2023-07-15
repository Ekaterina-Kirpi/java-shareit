package ru.practicum.shareit.validation.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String s) {
        super(s);
    }
}