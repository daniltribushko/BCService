package ru.tdd.geo.application.models.exceptions.geo.locations;

import ru.tdd.geo.application.models.exceptions.NotFoundException;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class LocationByIdNotFoundException extends NotFoundException {

    public LocationByIdNotFoundException() {
        super("Локация с указанным идентификатором не найдена");
    }
}
