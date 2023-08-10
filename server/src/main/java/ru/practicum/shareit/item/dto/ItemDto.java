package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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