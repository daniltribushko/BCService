package ru.tdd.user.application.security;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.tdd.user.application.models.exceptions.AuthenticationException;
import ru.tdd.user.database.entities.user.SystemUser;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.02.2026
 * Набор методов для работы с безопасностью пользователей
 */
public class UserSecurity {

    private UserSecurity(){}

    /** Проверка, что пользователь не менят чужого пользователя */
    public static void isCurrentUserOwner(UUID id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof SystemUser user && !Objects.equals(user.getId(), id)) {
            throw new AuthenticationException("Пользователь пытается изменить другого пользователя");
        }
    }
}
