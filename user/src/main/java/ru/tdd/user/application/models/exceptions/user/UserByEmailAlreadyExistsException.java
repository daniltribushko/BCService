package ru.tdd.user.application.models.exceptions.user;

import ru.tdd.user.application.models.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 03.02.2026
 */
public class UserByEmailAlreadyExistsException extends AlreadyExistsException {

    public UserByEmailAlreadyExistsException() {
        super("Пользователь с указанным электронным адресом уже создан");
    }
}
