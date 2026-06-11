package ru.tdd.bc.http.book;

import ru.tdd.bc.http.NotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class BookByIdNotFoundException extends NotFoundException {
    public BookByIdNotFoundException(UUID id) {
        super(getErrorText(id));
    }

    public static String getErrorText(UUID id) {
        return "Книга с идентификатором: \"%s\" не найдена".formatted(id);
    }
}
