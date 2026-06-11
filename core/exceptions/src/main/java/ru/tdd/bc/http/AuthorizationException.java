package ru.tdd.bc.http;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 */
public class AuthorizationException extends ApiException {

    public AuthorizationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
