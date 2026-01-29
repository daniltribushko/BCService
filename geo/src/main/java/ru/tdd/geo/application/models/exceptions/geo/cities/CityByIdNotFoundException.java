package ru.tdd.geo.application.models.exceptions.geo.cities;

import ru.tdd.geo.application.models.exceptions.NotFoundException;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 */
public class CityByIdNotFoundException extends NotFoundException {

    public CityByIdNotFoundException() {
        super("Город с указанным идентификатором не найден");
    }
}
