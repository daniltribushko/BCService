package ru.tdd.user.application.models.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Tribushko Danil
 * @since 03.02.2026
 * Dto запроса на регистрацию пользователя
 */
public class SignUp {

    @NotBlank(message = "Имя пользователя обязательно для заполнения")
    private String username;

    private String email;

    private Long chatId;

    @NotBlank(message = "Пароль обязателен для заполнения")
    private String password;

    public SignUp() {}

    public SignUp(String username, String email, Long chatId, String password) {
        this.username = username;
        this.email = email;
        this.chatId = chatId;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
