package ru.practicum.shareit.validation.exception;

public class DataException extends RuntimeException {
    public DataException(String type, Long id) {
        super("Не найден " + type + " c id: " + id);
    }

}
