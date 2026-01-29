package ru.tdd.geo.application.models.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * Исключение api приложения
 */
public class ApiException extends RuntimeException {

    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.statusCode = status.value();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
