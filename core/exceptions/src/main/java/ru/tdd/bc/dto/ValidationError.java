package ru.tdd.bc.dto;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 * Модель ошибки, которая возникает во время валидации
 */
public class ValidationError {

    private String fieldName;

    private String text;

    public ValidationError() {}

    public ValidationError(String text) {
        this.text = text;
    }

    public ValidationError(String fieldName, String text) {
        this.fieldName = fieldName;
        this.text = text;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

