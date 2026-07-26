package ru.tdd.bc.http.authors;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class AuthorByIdNotFoundException extends NotFoundException {
    public AuthorByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Автор с идентификатором: \"%s\" не найден".formatted(id);
    }
}
