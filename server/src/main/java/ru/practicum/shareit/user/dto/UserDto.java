package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
public class UserDto {
    @Size(max = 255)
    @NotEmpty(message = "Нужно представиться :)")
    private String name;
    @Email(message = "email заполнен некорректно")
    @NotEmpty(message = "email должен быть заполнен")
    private String email;
}