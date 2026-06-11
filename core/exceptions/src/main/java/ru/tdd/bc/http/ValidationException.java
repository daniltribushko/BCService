package ru.tdd.bc.http;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
