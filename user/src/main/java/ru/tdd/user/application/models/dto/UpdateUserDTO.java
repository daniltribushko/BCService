package ru.tdd.user.application.models.dto;

/**
 * @author Tribushko Danil
 * @since 11.02.2026
 * DTO запроса на обновление пользователя
 */
public class UpdateUserDTO {

    private String username;

    private String email;

    private String password;

    public UpdateUserDTO() {}

    public UpdateUserDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
