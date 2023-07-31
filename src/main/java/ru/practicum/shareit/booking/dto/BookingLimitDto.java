package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingLimitDto {
    private final Long id;
    private final Long bookerId;
}