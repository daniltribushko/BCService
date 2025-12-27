package ru.tdd.telegram_bot.model.dto;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 */
public class BooleanDTO {

    private Boolean result;

    public BooleanDTO(){}

    public BooleanDTO(Boolean result) {
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
