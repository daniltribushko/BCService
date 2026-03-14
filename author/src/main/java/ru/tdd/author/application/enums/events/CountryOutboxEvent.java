package ru.tdd.author.application.enums.events;

import ru.tdd.author.application.enums.OutboxEventType;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
 * Типы событий стран в кафка
 */
public enum CountryOutboxEvent implements OutboxEventType {
    CREATE("CREATE_COUNTRY"),
    UPDATE("UPDATE_COUNTRY"),
    DELETE("DELETE_COUNTRY");

    private final String type;

    CountryOutboxEvent(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
