package ru.tdd.user.application.models.exceptions.user;

import ru.tdd.user.application.models.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 03.02.2026
 */
public class UserByUsernameAlreadyExistsException extends AlreadyExistsException {

    public UserByUsernameAlreadyExistsException() {
        super("Пользователь с указанным именем уже создан");
    }
}
