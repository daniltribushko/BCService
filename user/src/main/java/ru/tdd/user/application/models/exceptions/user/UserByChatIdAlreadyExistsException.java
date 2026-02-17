package ru.tdd.user.application.models.exceptions.user;

import ru.tdd.user.application.models.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 03.02.2026
 */
public class UserByChatIdAlreadyExistsException extends AlreadyExistsException {

    public UserByChatIdAlreadyExistsException() {
        super("Пользаватель с указанным идентификатором телеграма уже найден");
    }
}
