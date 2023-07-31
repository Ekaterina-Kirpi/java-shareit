package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserListDto {
    @JsonValue //представить объект как одно простое значение
    private List<UserDtoResponse> users;
}