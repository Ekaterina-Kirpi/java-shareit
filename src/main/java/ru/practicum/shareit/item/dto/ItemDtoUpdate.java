package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Builder
@Data
public class ItemDtoUpdate {
    @Size(max = 255)
    private String name;
    @Size(max = 1000)
    private String description;
    private Boolean available;
}