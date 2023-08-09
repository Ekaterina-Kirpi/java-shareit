package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class ItemDto {
    @Size(max = 255)
    @NotBlank(message = "Нужно представиться :)")
    @NotNull
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное описание")
    private String name;
    @Size(max = 1000)
    @NotNull
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное описание")
    @NotBlank(message = "Описание должно быть заполнено")
    private String description;
    @NotNull(message = "Поле не должно быть пустым")
    private Boolean available;
    @Min(1)
    private Long requestId;
}