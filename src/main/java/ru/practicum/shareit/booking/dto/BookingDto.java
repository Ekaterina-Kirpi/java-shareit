package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enam.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utilits.Constants.DATE_PATTERN;

@Data
@Builder
public class BookingDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @NotNull(message = "Укажите дату, поле не может быть пустым")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @Future(message = "Дата завершения не может быть в прошлом")
    @NotNull(message = "Укажите дату, поле не может быть пустым")
    private LocalDateTime end;
    @NotNull(message = "Поле не может быть пустым")
    @Min(value = 1, message = "Некорректный itemId")
    private Long itemId;
    private Status status;
}