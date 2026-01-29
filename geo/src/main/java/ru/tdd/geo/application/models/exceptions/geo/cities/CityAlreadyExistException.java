package ru.tdd.geo.application.models.exceptions.geo.cities;

import ru.tdd.geo.application.models.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class CityAlreadyExistException extends AlreadyExistsException {

    public CityAlreadyExistException() {
        super("Город с указанным названием, страной, регионом уже создан");
    }
}
