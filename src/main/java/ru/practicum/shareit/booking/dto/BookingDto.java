package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enam.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @NotNull(message = "Укажите дату, поле не может быть пустым")
    @FutureOrPresent(message ="Дата начала не может быть в прошлом")
    private LocalDateTime start;
    @Future(message = "Дата завершения не может быть в прошлом")
    @NotNull(message = "Укажите дату, поле не может быть пустым")
    private LocalDateTime end;
    @NotNull(message =  "Поле не может быть пустым")
    @Min(value = 1, message = "Некорректный itemId")
    private Long itemId;
    private Status status;
}