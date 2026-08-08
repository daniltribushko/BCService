package ru.tdd.bc.book.application.exceptions;

import ru.tdd.bc.http.NotFoundException;

/**
 * @author Tribushko Danil
 * @since 02.08.2026
 */
public class PublisherByIdNotFoundException extends NotFoundException {

    public PublisherByIdNotFoundException() {
        super(getErrorText());
    }

    public static String getErrorText() {
        return "Издатель не найден";
    }
}
