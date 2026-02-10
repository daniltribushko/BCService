package ru.tdd.user.application.models.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 01.02.2026
 */
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(HttpStatus.NO_CONTENT.value(), message);
    }
}
