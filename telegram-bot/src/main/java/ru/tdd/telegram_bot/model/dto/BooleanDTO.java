package ru.tdd.telegram_bot.model.dto;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 */
public class BooleanDTO {

    private boolean result;

    public BooleanDTO(){}

    public BooleanDTO(Boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
