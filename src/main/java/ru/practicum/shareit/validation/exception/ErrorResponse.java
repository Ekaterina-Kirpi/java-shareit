package ru.practicum.shareit.validation.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private int number;
    private String status;
    private String error;
}
