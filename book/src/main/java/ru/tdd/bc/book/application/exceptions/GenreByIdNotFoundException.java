package ru.tdd.bc.book.application.exceptions;

import ru.tdd.bc.http.NotFoundException;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 */
public class GenreByIdNotFoundException extends NotFoundException {

    public GenreByIdNotFoundException() {
        super(getErrorText());
    }

    public static String getErrorText() {
        return "Жанр не найден";
    }
}
