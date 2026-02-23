package ru.tdd.author.application.dto;

import java.time.LocalDateTime;

/**
 * @author Tribusko Danil
 * @since 22.02.2026
 * DTO исключения
 */
public class ExceptionDTO {

    private int statusCode;

    private String message;

    private LocalDateTime timestamp;

    public ExceptionDTO() {}

    public ExceptionDTO(int statusCode, String message, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
