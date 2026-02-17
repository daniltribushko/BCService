package ru.tdd.user.application.models.exceptions.user;

import ru.tdd.user.application.models.exceptions.NotFoundException;

/**
 * @author Tribushko Danil
 * @since 12.02.2026
 */
public class UserByIdNotFoundException extends NotFoundException {
    public UserByIdNotFoundException() {
        super("Пользователь по указанному идентификатору не найден");
    }
}
