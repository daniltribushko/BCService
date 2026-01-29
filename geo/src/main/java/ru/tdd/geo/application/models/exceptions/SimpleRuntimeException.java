package ru.tdd.geo.application.models.exceptions;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 */
public class SimpleRuntimeException extends RuntimeException {

    public SimpleRuntimeException(String message) {
        super(message);
    }
}
