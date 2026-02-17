package ru.tdd.user.application.models.dto;

import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.02.2026
 * DTO пользователя
 */
public class UserDTO {

    private UUID id;

    private String username;

    private String email;

    private Long chatId;

    private List<Role> roles;

    public UserDTO() {}

    public UserDTO(UUID id, String username, String email, Long chatId, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.chatId = chatId;
        this.roles = roles;
    }

    public static UserDTO mapFromEntity(SystemUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles());

        if (user instanceof AppUser appUser) {
            dto.setEmail(appUser.getEmail());
            dto.setChatId(appUser.getChatId());
        }

        return dto;
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
}
