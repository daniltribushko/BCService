package ru.tdd.user.application.models.dto;

import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 12.02.2026
 * DTO детальной информации пользователя системы
 */
public class UserDetailsDTO {

    private UUID id;

    private String username;

    private String email;

    private Long chatId;

    private List<Role> roles;

    private LocalDateTime lastDateOnline;

    private LocalDateTime creationTime;

    private LocalDateTime updateTime;

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(UUID id, String username, String email, Long chatId, List<Role> roles, LocalDateTime lastDateOnline, LocalDateTime creationTime, LocalDateTime updateTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.chatId = chatId;
        this.roles = roles;
        this.lastDateOnline = lastDateOnline;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String username;
        private String email;
        private Long chatId;
        private List<Role> roles;
        private LocalDateTime lastDateOnline;
        private LocalDateTime creationTime;
        private LocalDateTime updateTime;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder roles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder lastDateOnline(LocalDateTime lastDateOnline) {
            this.lastDateOnline = lastDateOnline;
            return this;
        }

        public Builder creationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public UserDetailsDTO build() {
            UserDetailsDTO user = new UserDetailsDTO();
            user.id = this.id;
            user.email = this.email;
            user.chatId = this.chatId;
            user.username = this.username;
            user.roles = this.roles;
            user.lastDateOnline = this.lastDateOnline;
            user.creationTime = this.creationTime;
            user.updateTime = this.updateTime;
            return user;
        }
    }

    public static UserDetailsDTO mapFromEntity(SystemUser user) {
        Builder userBuilder = UserDetailsDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles())
                .lastDateOnline(user.getLastDateOnline())
                .creationTime(user.getCreationTime())
                .updateTime(user.getUpdateTime());

        if (user instanceof AppUser appUser) {
            userBuilder.email(appUser.getEmail())
                    .chatId(appUser.getChatId());
        }

        return userBuilder.build();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public LocalDateTime getLastDateOnline() {
        return lastDateOnline;
    }

    public void setLastDateOnline(LocalDateTime lastDateOnline) {
        this.lastDateOnline = lastDateOnline;
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
}
