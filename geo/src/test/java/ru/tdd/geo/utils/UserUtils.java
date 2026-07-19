package ru.tdd.geo.utils;

import ru.tdd.bc.security.dto.Role;
import ru.tdd.bc.security.dto.UserDto;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 18.07.2026
 * Набор утилит для работы с пользователями
 */
public class UserUtils {

    private UserUtils() {}

    public static final UserDto USER =
            UserDto.builder()
                    .username("user")
                    .roles(List.of(Role.USER))
                    .build();

    public static final UserDto ADMIN =
            UserDto.builder()
                    .username("admin")
                    .roles(List.of(Role.USER, Role.ADMIN))
                    .build();
}
