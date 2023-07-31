package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-item-requests.
 */

@Builder
@Data
@Jacksonized
public class ItemRequestDto {
    @NotBlank(message = "Нужно заполнить поле text")
    @Size(max = 1000, message = "Максимальное колличество символов: 1000")
    private String description;
}