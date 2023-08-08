package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class BookingListDto {
    @JsonValue
    private List<BookingDtoResponse> bookings;
}