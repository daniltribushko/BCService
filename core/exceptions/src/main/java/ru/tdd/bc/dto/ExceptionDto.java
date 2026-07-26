package ru.tdd.bc.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 01.05.2026
 * DTO исключения
 */
public class ExceptionDto {

    private HttpStatus statusCode;

    private String message;

    private LocalDateTime timestamp;

    public ExceptionDto() {}

    public ExceptionDto(HttpStatus statusCode, String message, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
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
