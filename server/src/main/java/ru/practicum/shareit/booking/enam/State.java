package ru.practicum.shareit.booking.enam;

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
        for (State state : values()) {
            if (state.toString().equals(value)) {
                return state;
            }
        }
        return UNSUPPORTED_STATUS;
    }
}