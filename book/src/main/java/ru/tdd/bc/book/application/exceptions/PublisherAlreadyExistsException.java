package ru.tdd.bc.book.application.exceptions;

import ru.tdd.bc.http.AlreadyExistsException;

/**
 * @author Tribushko Danil
 * @since 02.08.2026
 */
public class PublisherAlreadyExistsException extends AlreadyExistsException {
    public PublisherAlreadyExistsException() {
        super(getErrorText());
    }

    public static String getErrorText() {
        return "Издатель уже создан";
    }
}
