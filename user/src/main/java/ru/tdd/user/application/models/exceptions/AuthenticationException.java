package ru.tdd.user.application.models.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 14.02.2026
 */
public class AuthenticationException extends ApiException {
    public AuthenticationException(String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
    }
}
