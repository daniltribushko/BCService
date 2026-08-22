package ru.tdd.bc.book.application.exceptions;

import ru.tdd.bc.http.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 16.08.2026
 */
public class AuthorAlreadyExistsException extends AlreadyExistsException {
    public AuthorAlreadyExistsException() {
        super(getErrorText());
    }

    public static String getErrorText() {
        return "Автор уже создан";
    }
}
