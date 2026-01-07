package ru.tdd.geo.application.models.dto;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * DTO исключения приложения
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
