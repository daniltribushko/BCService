package ru.tdd.author.application.exceptions.country;

import ru.tdd.author.application.exceptions.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
public class CountryByIdNotFoundException extends NotFoundException {

    public CountryByIdNotFoundException(UUID id) {
        super("Страна с идентификатором: " + id + " не найдена");
    }
}
