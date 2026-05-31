package ru.tdd.core.application.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 10.05.2026
 */
public class AuthorizationException extends ApiException {
    public AuthorizationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
