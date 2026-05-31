package ru.tdd.core.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 01.05.2026
 * DTO исключения
 */
@Schema(description = "Dto исключения приложения")
public class ExceptionDTO {

    @Schema(
            name = "statusCode",
            description = "Код ошибки",
            type = "int64",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "400"
    )
    private int statusCode;

    @Schema(
            name = "message",
            description = "Текст ошибки",
            type = "string",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "Неверный запрос"
    )
    private String message;

    @Schema(
            name = "timestamp",
            description = "Время появления ошибки",
            type = "string",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "date-time",
            example = "2023-01-31T12:00:00"
    )
    private LocalDateTime timestamp;

    public ExceptionDTO() {}

    public ExceptionDTO(int statusCode, String message, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
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
}
