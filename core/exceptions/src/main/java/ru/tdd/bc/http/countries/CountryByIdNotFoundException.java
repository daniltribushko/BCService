package ru.tdd.bc.http.countries;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class CountryByIdNotFoundException extends NotFoundException {
    public CountryByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Страна с идентификатором: \"%s\" не найдена".formatted(id);
    }
}
