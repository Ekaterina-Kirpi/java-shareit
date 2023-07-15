package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.validation.exception.ArgumentException;

public enum BookingState {
    ALL, //получение списка всех бронирований текущего пользователя.
    CURRENT,//текущие бронирования
    PAST,//завершённые бронирования
    FUTURE,//будущие бронирования
    WAITING,//ожидающие подтверждения бронирования
    REJECTED, //отклонённые бронирования
    UNSUPPORTED_STATUS;


    public static BookingState checkState(String state) {
        try {
            return BookingState.valueOf(state);

        } catch (Exception e) {
            String message = "Unknown state: " + state;
            throw new ArgumentException(message);
        }
    }
}
