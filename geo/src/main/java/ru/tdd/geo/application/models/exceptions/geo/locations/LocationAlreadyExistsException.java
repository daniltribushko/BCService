package ru.tdd.geo.application.models.exceptions.geo.locations;

import ru.tdd.bc.http.AlreadyExistsException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class LocationAlreadyExistsException extends AlreadyExistsException {

    public LocationAlreadyExistsException(String name, UUID cityId) {
        super(getErrorText(name, cityId));
    }

    public static String getErrorText(String name, UUID cityId) {
        return "Город с названием: \"%s\" и городом:\"%s\" уже создан".formatted(name, cityId);
    }
}
