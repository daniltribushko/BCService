package ru.tdd.bc.http.genres;

import ru.tdd.bc.http.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class GenreAlreadyExistsException extends AlreadyExistsException {
    public GenreAlreadyExistsException(String name) {
        super(getErrorText(name));
    }

    public static String getErrorText(String name) {
        return "Жанр: \"%s\" уже создан".formatted(name);
    }
}
