package ru.tdd.telegram_bot.model.dto;

import ru.tdd.telegram_bot.controller.annotations.LocalDateTimeJsonFormat;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Dto исключения
 */
public class ExceptionDto {

    private String message;

    @LocalDateTimeJsonFormat
    private LocalDateTime timestamp;

    public ExceptionDto() {

    }

    public ExceptionDto(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
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
