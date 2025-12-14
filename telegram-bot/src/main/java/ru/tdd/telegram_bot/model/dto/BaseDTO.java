package ru.tdd.telegram_bot.model.dto;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Dto объекта имеющего одно поле
 */
public class BaseDTO {

    private String name;

    public BaseDTO() {}

    public BaseDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
