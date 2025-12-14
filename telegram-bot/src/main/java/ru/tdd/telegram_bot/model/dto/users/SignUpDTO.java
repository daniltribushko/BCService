package ru.tdd.telegram_bot.model.dto.users;

import ru.tdd.telegram_bot.controller.annotations.LocalDateJsonFormat;

import java.time.LocalDate;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Дто регистрации пользователей
 */
public class SignUpDTO {

    private Long chatId;

    private String username;

    @LocalDateJsonFormat
    private LocalDate birthday;

    public SignUpDTO() {}

    public SignUpDTO(Long chatId, String username, LocalDate birthday) {
        this.username = username;
        this.chatId = chatId;
        this.birthday = birthday;
    }

    public SignUpDTO(Long chatId, String username) {
        this.username = username;
        this.chatId = chatId;
    }

    public SignUpDTO(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }


}
