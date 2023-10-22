package ru.zenclass.ylab.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterPlayerDTO {
    @NotNull(message = "Имя пользователя обязательно для заполнения.")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов.")
    private String username;

    @NotNull(message = "Пароль обязателен для заполнения.")
    @Size(min = 6, message = "Пароль должен содержать не менее 8 символов.")
    private String password;
}
