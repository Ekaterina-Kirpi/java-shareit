package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Builder
@Getter
public class UserDtoUpdate {
    @Size(max = 255)
    private String name;
    @Email(message = "email заполнен некорректно")
    private String email;
}