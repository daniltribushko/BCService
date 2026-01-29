package ru.tdd.geo.application.models.exceptions.geo.country;

import ru.tdd.geo.application.models.exceptions.NotFoundException;

/**
 * @author Tribushko Danil
 * @since 08.01.2026
 */
public class CountryByIdNotFoundException extends NotFoundException {

    public CountryByIdNotFoundException() {
        super("Страна с указанным идентификатором не найдена");
    }
}
