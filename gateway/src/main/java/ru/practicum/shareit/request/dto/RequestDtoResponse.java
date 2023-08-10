package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class RequestDtoResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;

}