package ru.tdd.user.application.models.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 01.02.2026
 */
public class AlreadyExistsException extends ApiException {

    public AlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT.value(), message);
    }
}
