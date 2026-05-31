package ru.tdd.book.utils;

import ru.tdd.core.controller.dto.users.Role;
import ru.tdd.core.controller.dto.users.UserDto;

import java.util.List;
import java.util.UUID;

public class UserUtils {

    private UserUtils() {}

    public static final UserDto ADMIN = new UserDto(
            UUID.randomUUID(),
            "admin",
            List.of(Role.USER, Role.ADMIN)
    );

    public static final UserDto USER = new UserDto(
            UUID.randomUUID(),
            "user",
            List.of(Role.USER)
    );
}
