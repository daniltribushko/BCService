package ru.tdd.user.application.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Tribushko Danil
 * @since 03.02.2026
 * Dto запроса на авторизацию пользователя
 */
public class SignIn {

    @Schema(
            name = "username",
            description = "Имя пользователя",
            type = "string",
            example = "user"
    )
    @NotBlank(message = "Имя пользователя обязательно для заполнения")
    private String username;

    @Schema(
            name = "password",
            description = "Пароль пользователя",
            type = "string",
            example = "123"
    )
    @NotBlank(message = "Пароль обязателен для заполнения")
    private String password;

    public SignIn() {}

    public SignIn(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
