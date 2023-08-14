package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemLimitDto;
import ru.practicum.shareit.user.dto.UserLimitDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemLimitDto item;
    private UserLimitDto booker;
    private Status status;
}