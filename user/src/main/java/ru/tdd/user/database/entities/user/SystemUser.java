package ru.tdd.user.database.entities.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.database.converters.RoleConverter;
import ru.tdd.user.database.entities.EntityVersion;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 29.01.2026
 * Системный пользователь системы
 */
@Entity
@Table(name = "app_system_user")
@Inheritance(strategy = InheritanceType.JOINED)
public class SystemUser extends EntityVersion implements UserDetails {

    @Column(name = "username", nullable = false, unique = true)
    protected String username;

    @Column(name = "password", nullable = false)
    protected String password;

    @Column(name = "last_date_online", nullable = false)
    protected LocalDateTime lastDateOnline = LocalDateTime.now();

    @Column(name = "roles", nullable = false)
    @Convert(converter = RoleConverter.class)
    protected List<Role> roles;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;

        private String username;

        private String password;

        private LocalDateTime lastDateOnline = LocalDateTime.now();

        private LocalDateTime creationTime = LocalDateTime.now();

        private LocalDateTime updateTime = LocalDateTime.now();

        private List<Role> roles;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
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

        public Builder creationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder roles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public SystemUser build() {
            SystemUser user = new SystemUser();

            user.id = this.id;
            user.username = this.username;
            user.password = this.password;
            user.lastDateOnline = this.lastDateOnline;
            user.creationTime = this.creationTime;
            user.updateTime = this.updateTime;
            user.roles = this.roles;

            return user;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastDateOnline() {
        return lastDateOnline;
    }

    public void setLastDateOnline(LocalDateTime lastDateOnline) {
        this.lastDateOnline = lastDateOnline;
    }

}
