package ru.practicum.shareit.exception;

public class ExceptionEmail extends RuntimeException {
    public ExceptionEmail(String message) {
        super(message);
    }
}