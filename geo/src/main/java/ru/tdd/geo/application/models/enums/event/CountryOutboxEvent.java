package ru.tdd.geo.application.models.enums.event;

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
