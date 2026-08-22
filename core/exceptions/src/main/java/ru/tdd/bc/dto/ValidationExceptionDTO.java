package ru.tdd.bc.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 */
public class ValidationExceptionDTO {

    private HttpStatus statusCode;

    private List<ValidationError> errors;

    private LocalDateTime timestamp;

    public ValidationExceptionDTO() {}

    public ValidationExceptionDTO(HttpStatus statusCode, List<ValidationError> errors, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
