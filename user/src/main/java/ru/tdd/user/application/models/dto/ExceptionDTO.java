package ru.tdd.user.application.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 10.02.2026
 * DTO исключения приложения
 */
public class ExceptionDTO {

    @Schema(
            name = "statusCode",
            description = "Код ошибки",
            type = "int32",
            example = "400"
    )
    private int statusCode;

    @Schema(
            name = "message",
            description = "Текст ошибки",
            type = "string",
            example = "Неверный запрос"
    )
    private String message;

    @Schema(
            name = "timestamp",
            description = "Время ошибки",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime timestamp;

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
