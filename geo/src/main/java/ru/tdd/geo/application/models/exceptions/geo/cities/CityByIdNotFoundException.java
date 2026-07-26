package ru.tdd.geo.application.models.exceptions.geo.cities;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class CityByIdNotFoundException extends NotFoundException {

    public CityByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Город с идентификатором: \"%s\" не найден".formatted(id);
    }
}
