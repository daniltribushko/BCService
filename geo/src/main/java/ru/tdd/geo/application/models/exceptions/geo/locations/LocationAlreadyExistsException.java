package ru.tdd.geo.application.models.exceptions.geo.locations;

import ru.tdd.geo.application.models.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class LocationAlreadyExistsException extends AlreadyExistsException {

    public LocationAlreadyExistsException() {
        super("Локация с указанным названием и городом уже создана");
    }
}
