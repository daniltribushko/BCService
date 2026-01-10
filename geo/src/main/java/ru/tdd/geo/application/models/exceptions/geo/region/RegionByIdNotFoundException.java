package ru.tdd.geo.application.models.exceptions.geo.region;

import ru.tdd.geo.application.models.exceptions.NotFoundException;

/**
 * @author Tribushko Danil
 * @since 08.01.2026
 */
public class RegionByIdNotFoundException extends NotFoundException {

    public RegionByIdNotFoundException() {
        super("Регион с указанным идентификатором не найден");
    }
}
