package ru.tdd.bc.http.users;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class UserByIdNotFoundException extends NotFoundException {
    public UserByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Пользователь с идентификатором: \"%s\" не найден".formatted(id);
    }
}
