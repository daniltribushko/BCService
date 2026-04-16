package ru.tdd.geo.application.models.enums.event;

import ru.tdd.core.application.enums.kafka_events.OutboxEventType;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Типы событий стран для кафки
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
