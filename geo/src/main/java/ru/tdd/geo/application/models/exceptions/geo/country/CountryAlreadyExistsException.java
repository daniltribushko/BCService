package ru.tdd.geo.application.models.exceptions.geo.country;

import ru.tdd.geo.application.models.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 08.01.2026
 */
public class CountryAlreadyExistsException extends AlreadyExistsException {

    public CountryAlreadyExistsException() {
        super("Страна с указанным названием уже создана");
    }
}
