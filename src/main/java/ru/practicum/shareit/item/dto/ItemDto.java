package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingLimitDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Нужно представиться :)")
    private String name;

    @NotBlank(message = "Описание должно быть заполнено.")
    private String description;

    @NotNull(message = "Поле не должно быть пустым.")
    private Boolean available;
    private BookingLimitDto lastBooking;
    private BookingLimitDto nextBooking;
    private List<CommentDto> comments;
}