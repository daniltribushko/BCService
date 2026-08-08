package ru.tdd.bc.book.application.exceptions;

import ru.tdd.bc.http.NotFoundException;

/**
 * @author Tribusko Danil
 * @since 06.08.2026
 */
public class AuthorByIdNotFoundException extends NotFoundException {
    public AuthorByIdNotFoundException() {
        super(getErrorText());
    }

    public static String getErrorText() {
        return "Автор не найден";
    }
}
