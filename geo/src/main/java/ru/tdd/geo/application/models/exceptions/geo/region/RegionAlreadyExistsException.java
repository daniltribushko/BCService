package ru.tdd.geo.application.models.exceptions.geo.region;

import ru.tdd.geo.application.models.exceptions.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 08.01.2026
 */
public class RegionAlreadyExistsException extends AlreadyExistsException {

    public RegionAlreadyExistsException() {
        super("Регион с указанным названием и страной уже создан");
    }
}
