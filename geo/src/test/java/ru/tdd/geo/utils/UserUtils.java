package ru.tdd.geo.utils;

import ru.tdd.bc.security.dto.Role;
import ru.tdd.bc.security.dto.UserDto;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 18.07.2026
 * Набор утилит для работы с пользователями
 */
public class UserUtils {

    private UserUtils() {}

    public static final UserDto USER =
            UserDto.builder()
                    .id(UUID.fromString("5c3afec6-74fd-4aec-8c1a-4d407eda09f7"))
                    .username("user")
                    .roles(List.of(Role.USER))
                    .build();

    public static final UserDto ADMIN =
            UserDto.builder()
                    .id(UUID.fromString("50a8adaa-b5fb-4582-bca4-991cefbca53b"))
                    .username("admin")
                    .roles(List.of(Role.USER, Role.ADMIN))
                    .build();
}
