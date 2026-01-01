package ru.tdd.telegram_bot.model.dto.users;

import ru.tdd.telegram_bot.controller.annotations.LocalDateJsonFormat;
import ru.tdd.telegram_bot.controller.annotations.LocalDateTimeJsonFormat;
import ru.tdd.telegram_bot.model.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Dto класс пользователя
 */
public class UserDTO {

    private UUID id;

    private Long chatId;

    private String username;

    @LocalDateJsonFormat
    private LocalDate birthday;

    @LocalDateTimeJsonFormat
    private LocalDateTime creationTime;

    @LocalDateTimeJsonFormat
    private LocalDateTime updateTime;

    private List<Role> roles;

    public UserDTO() {}

    public UserDTO(UUID id, Long chatId, String username, LocalDate birthday, LocalDateTime creationTime, LocalDateTime updateTime, List<Role> roles) {
        this.id = id;
        this.chatId = chatId;
        this.username = username;
        this.birthday = birthday;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
