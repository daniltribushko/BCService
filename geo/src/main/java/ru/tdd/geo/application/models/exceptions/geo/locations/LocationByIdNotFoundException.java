package ru.tdd.geo.application.models.exceptions.geo.locations;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class LocationByIdNotFoundException extends NotFoundException {

    public LocationByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Локация с идентификатором: \"%s\" не найдена".formatted(id);
    }
}
