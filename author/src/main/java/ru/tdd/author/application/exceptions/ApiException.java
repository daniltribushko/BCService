package ru.tdd.author.application.exceptions;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Исключение api приложения
 */
public abstract class ApiException extends RuntimeException {

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
