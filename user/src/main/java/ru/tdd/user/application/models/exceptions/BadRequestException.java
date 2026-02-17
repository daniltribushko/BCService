package ru.tdd.user.application.models.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 01.02.2026
 */
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}
