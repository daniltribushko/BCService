package ru.tdd.bc.book.application.exceptions;

import ru.tdd.bc.http.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 */
public class GenreAlreadyExistsException extends AlreadyExistsException {

    public GenreAlreadyExistsException() {
        super(getErrorText());
    }

    public static String getErrorText() {
        return "Жанр уже создан";
    }
}
