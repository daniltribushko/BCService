package ru.tdd.geo.application.models.exceptions.geo.region;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 08.01.2026
 */
public class RegionByIdNotFoundException extends NotFoundException {

    public RegionByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Регион с идентификатором: \"%s\" не найден".formatted(id);
    }
}
