package ru.tdd.geo.application.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.tdd.geo.application.models.enums.Role;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO пользователя
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return "";
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
