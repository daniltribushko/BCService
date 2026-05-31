package ru.tdd.book.domain.exceptions.genre;

import ru.tdd.core.application.exceptions.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 */
public class GenreByIdNotFoundException extends NotFoundException {
    public GenreByIdNotFoundException(UUID id) {
        super("Жанр с идентификатором: \"%s\" не найден".formatted(id));
    }
}
