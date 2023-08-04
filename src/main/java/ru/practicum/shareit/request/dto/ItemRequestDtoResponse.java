package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
@Builder
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
}