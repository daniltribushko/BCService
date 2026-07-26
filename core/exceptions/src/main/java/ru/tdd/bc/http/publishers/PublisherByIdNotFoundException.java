package ru.tdd.bc.http.publishers;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class PublisherByIdNotFoundException extends NotFoundException {
    public PublisherByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Издатель с идентификатором: \"%s\" не найден".formatted(id);
    }
}
