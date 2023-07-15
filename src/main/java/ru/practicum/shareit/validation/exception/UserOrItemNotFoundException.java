package ru.practicum.shareit.validation.exception;

public class UserOrItemNotFoundException extends RuntimeException {
    public UserOrItemNotFoundException(String s) {
        super(s);
    }
}
