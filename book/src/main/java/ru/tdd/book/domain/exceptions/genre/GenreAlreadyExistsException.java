package ru.tdd.book.domain.exceptions.genre;

import ru.tdd.core.application.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 */
public class GenreAlreadyExistsException extends AlreadyExistsException {
    public GenreAlreadyExistsException(String name) {
        super("Жанр: \"%s\" уже создан".formatted(name));
    }
}
