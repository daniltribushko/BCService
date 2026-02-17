package ru.tdd.user.database.entities.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import ru.tdd.user.application.models.enums.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 31.01.2026
 * Пользователь приложения
 */
@Entity
@Table(name = "app_user")
@PrimaryKeyJoinColumn(name = "id")
public class AppUser extends SystemUser {

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    public AppUser() {
    }

    public static Builder appUserBuilder() {
        return new Builder();
    }

    public static class Builder {

        private UUID id;

        private String username;

        private String email;

        private Long chatId;

        private String password;

        private LocalDateTime lastDateOnline = LocalDateTime.now();

        private List<Role> roles;

        private LocalDateTime creationTime = LocalDateTime.now();

        private LocalDateTime updateTime = LocalDateTime.now();

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

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder lastDateOnline(LocalDateTime lastDateOnline) {
            this.lastDateOnline = lastDateOnline;
            return this;
        }

        public Builder roles(List<Role> roles) {
            this.roles = roles;
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

        public AppUser build() {
            AppUser user = new AppUser();

            user.id = this.id;
            user.username = this.username;
            user.email = this.email;
            user.chatId = this.chatId;
            user.password = this.password;
            user.lastDateOnline = this.lastDateOnline;
            user.roles = this.roles;
            user.creationTime = this.creationTime;
            user.updateTime = this.updateTime;

            return user;
        }
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
}
