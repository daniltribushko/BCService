package ru.tdd.author.application.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.tdd.author.application.enums.Role;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * DTO пользователя
 */
public class UserDTO implements UserDetails {

    private UUID id;

    private Long chatId;

    private String username;

    private List<Role> roles;

    public UserDTO() {}

    public UserDTO(UUID id, Long chatId, String username, List<Role> roles) {
        this.id = id;
        this.chatId = chatId;
        this.username = username;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
