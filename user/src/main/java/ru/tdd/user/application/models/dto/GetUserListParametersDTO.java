package ru.tdd.user.application.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.user.application.models.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Tribushko Danil
 * @since 14.02.2026
 * Список параметров для получения списка пользователей
 */
public class GetUserListParametersDTO {

    @Schema(
            name = "username",
            description = "Имя пользователя",
            type = "String",
            example = "user"
    )
    private String username;

    @Schema(
            name = "email",
            description = "Электронный адрес пользователя",
            type = "string",
            example = "user@gmail.com"
    )
    private String email;

    @Schema(
            name = "roles",
            description = "Роли пользователя",
            type = "array",
            implementation = String.class,
            allowableValues = {"USER", "ADMIN"}
    )
    private List<Role> roles;

    @Schema(
            name = "creationTimeStart",
            description = "Минимальная дата создания пользователя",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime creationTimeStart;

    @Schema(
            name = "creationTimeEnd",
            description = "Максимальная дата создания пользователя",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime creationTimeEnd;

    @Schema(
            name = "updateTimeStart",
            description = "Минимальная дата обновления пользователя",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime updateTimeStart;

    @Schema(
            name = "updateTimeEnd",
            description = "Максимальная дата обновления пользователя",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime updateTimeEnd;

    @Schema(
            name = "lastDateOnlineStart",
            description = "Минимальная дата последнего входа пользователя",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime lastDateOnlineStart;

    @Schema(
            name = "lastDateOnlineEnd",
            description = "Максимальная дата последнего входа пользователя",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime lastDateOnlineEnd;

    public GetUserListParametersDTO() {}

    public GetUserListParametersDTO(
            String username,
            String email,
            List<Role> roles,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            LocalDateTime lastDateOnlineStart,
            LocalDateTime lastDateOnlineEnd
    ) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.creationTimeStart = creationTimeStart;
        this.creationTimeEnd = creationTimeEnd;
        this.updateTimeStart = updateTimeStart;
        this.updateTimeEnd = updateTimeEnd;
        this.lastDateOnlineStart = lastDateOnlineStart;
        this.lastDateOnlineEnd = lastDateOnlineEnd;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreationTimeStart() {
        return creationTimeStart;
    }

    public void setCreationTimeStart(LocalDateTime creationTimeStart) {
        this.creationTimeStart = creationTimeStart;
    }

    public LocalDateTime getCreationTimeEnd() {
        return creationTimeEnd;
    }

    public void setCreationTimeEnd(LocalDateTime creationTimeEnd) {
        this.creationTimeEnd = creationTimeEnd;
    }

    public LocalDateTime getUpdateTimeStart() {
        return updateTimeStart;
    }

    public void setUpdateTimeStart(LocalDateTime updateTimeStart) {
        this.updateTimeStart = updateTimeStart;
    }

    public LocalDateTime getUpdateTimeEnd() {
        return updateTimeEnd;
    }

    public void setUpdateTimeEnd(LocalDateTime updateTimeEnd) {
        this.updateTimeEnd = updateTimeEnd;
    }

    public LocalDateTime getLastDateOnlineStart() {
        return lastDateOnlineStart;
    }

    public void setLastDateOnlineStart(LocalDateTime lastDateOnlineStart) {
        this.lastDateOnlineStart = lastDateOnlineStart;
    }

    public LocalDateTime getLastDateOnlineEnd() {
        return lastDateOnlineEnd;
    }

    public void setLastDateOnlineEnd(LocalDateTime lastDateOnlineEnd) {
        this.lastDateOnlineEnd = lastDateOnlineEnd;
    }
}
