package ru.tdd.core.controller.dto;

/**
 * @author Tribushko Danil
 * @since 01.05.2026
 * DTO исключения
 */
public class ExceptionDTO {

    private int statusCode;

    private String message;

    public ExceptionDTO() {}

    public ExceptionDTO(int statusCode, String message) {
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
