package ru.tdd.bc.http.countries;

import ru.tdd.bc.http.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class CountryAlreadyExistsException extends AlreadyExistsException {
    public CountryAlreadyExistsException(String name) {
        super(getErrorText(name));
    }

    public static String getErrorText(String name) {
        return "Страна: \"%s\" уже создана".formatted(name);
    }
}
