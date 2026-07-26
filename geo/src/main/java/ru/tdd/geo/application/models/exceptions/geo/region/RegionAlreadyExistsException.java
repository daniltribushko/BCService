package ru.tdd.geo.application.models.exceptions.geo.region;

import ru.tdd.bc.http.AlreadyExistsException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 08.01.2026
 */
public class RegionAlreadyExistsException extends AlreadyExistsException {

    public RegionAlreadyExistsException(String name, UUID countryId) {
        super(getErrorText(name, countryId));
    }

    public static String getErrorText(String name, UUID countryId) {
        return "Регион с названием: \"%s\" и страной: \"%s\" уже создан".formatted(name, countryId);
    }
}
