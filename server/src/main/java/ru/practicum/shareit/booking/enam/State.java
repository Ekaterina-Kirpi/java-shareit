package ru.practicum.shareit.booking.enam;

import ru.practicum.shareit.exception.StateException;

/**
 * ALL  получение списка всех бронирований текущего пользователя.
 * CURRENT  текущие бронирования
 * PAST  завершённые бронирования
 * FUTURE  будущие бронирования
 * WAITING  ожидающие подтверждения бронирования
 * REJECTED  отклонённые бронирования
 * UNSUPPORTED_STATUS
 */

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED, UNSUPPORTED_STATUS;

    public static State checkState(String value) {
        try {
            return State.valueOf(value.toUpperCase());
        } catch (StateException e) {
            throw new StateException("Unknown state: " + value);
        }

    }

}