package ru.tdd.bc.book.utils;

import ru.tdd.bc.security.dto.Role;
import ru.tdd.bc.security.dto.UserDto;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 26.07.2026
 * Набор утилит для работы с пользователями
 */
public class UserUtils {

    private UserUtils() {
    }

    public static final UserDto USER = UserDto.builder()
            .id(UUID.fromString("cd7020a5-fdb2-4289-b224-ccfaf4944007"))
            .username("user")
            .roles(List.of(Role.USER))
            .build();

    public static final UserDto ADMIN = UserDto.builder()
            .id(UUID.fromString("14fe0b90-0b4e-41c1-941a-858b82490a41"))
            .username("admin")
            .roles(List.of(Role.ADMIN, Role.USER))
            .build();
}
