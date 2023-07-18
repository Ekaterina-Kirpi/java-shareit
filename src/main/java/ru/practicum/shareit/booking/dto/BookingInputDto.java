package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingInputDto {
    private long id;
    private long itemId;
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    @NotNull(message = "Укажите дату, поле не может быть пустым")
    private LocalDateTime start;
    @FutureOrPresent(message = "Дата завершения не может быть в прошлом")
    @NotNull(message = "Укажите дату, поле не может быть пустым")
    private LocalDateTime end;
}