package ru.tdd.core.application.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 14.03.2026
 * Базовый класс исключения с http статусом
 */
public class ApiException extends RuntimeException {

    protected final HttpStatus statusCode;

    protected final LocalDateTime timestamp;

    public ApiException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
