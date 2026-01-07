package ru.tdd.geo.application.models.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 06.01.2026
 */
public class AlreadyExistsException extends ApiException {

    public AlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
