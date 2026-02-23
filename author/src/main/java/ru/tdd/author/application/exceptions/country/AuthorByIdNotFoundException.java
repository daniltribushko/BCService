package ru.tdd.author.application.exceptions.country;

import ru.tdd.author.application.exceptions.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
public class AuthorByIdNotFoundException extends NotFoundException {

    public AuthorByIdNotFoundException(UUID id) {
        super("Автор с идентификатором: " + id + " не найден");
    }
}
