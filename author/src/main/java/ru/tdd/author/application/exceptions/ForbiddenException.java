package ru.tdd.author.application.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 */
public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
    }
}
