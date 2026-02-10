package ru.tdd.user.application.models.exceptions;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 01.02.2026
 * Класс-исключения api приложения
 */
public class ApiException extends RuntimeException {

    protected final int statusCode;

    protected final LocalDateTime timestamp;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
