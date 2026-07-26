package ru.tdd.geo.application.models.exceptions.geo.cities;

import ru.tdd.bc.http.AlreadyExistsException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class CityAlreadyExistException extends AlreadyExistsException {

    public CityAlreadyExistException(String name, UUID countryId, UUID regionId) {
        super(getErrorText(name, countryId, regionId));
    }

    public static String getErrorText(String name, UUID countryId, UUID regionId) {
        return "Город с названием: \"%s\", страной: \"%s\", регионом: \"%s\" уже создан".formatted(name, countryId, regionId);
    }
}
