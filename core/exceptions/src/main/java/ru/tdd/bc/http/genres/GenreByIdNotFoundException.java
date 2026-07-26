package ru.tdd.bc.http.genres;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class GenreByIdNotFoundException extends NotFoundException {
    public GenreByIdNotFoundException(UUID id) {
        super("Жанр с идентификатором: \"%s\" не найден".formatted(id));
    }
}
