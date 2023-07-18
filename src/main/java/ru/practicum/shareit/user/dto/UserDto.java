package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email(message = "email заполнен некорректно")
    @NotBlank(message = "email должен быть заполнен")
    private String email;
    @NotBlank(message = "Нужно представиться :)")
    private String name;
}
