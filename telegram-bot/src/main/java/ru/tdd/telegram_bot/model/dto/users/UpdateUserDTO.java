package ru.tdd.telegram_bot.model.dto.users;

import ru.tdd.telegram_bot.controller.annotations.LocalDateJsonFormat;

import java.time.LocalDate;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * DTO запроса на обновление пользователя
 */
public class UpdateUserDTO {

    private String username;

    @LocalDateJsonFormat
    private LocalDate birthday;

    public UpdateUserDTO(){}

    public UpdateUserDTO(String username, LocalDate birthday) {
        this.username = username;
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
