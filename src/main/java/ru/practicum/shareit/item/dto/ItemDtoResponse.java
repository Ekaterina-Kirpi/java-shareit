package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingLimitDto;

import java.util.Set;

@Builder
@Getter
@Setter
@ToString
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingLimitDto lastBooking;
    private BookingLimitDto nextBooking;
    private Set<CommentDtoResponse> comments;
}