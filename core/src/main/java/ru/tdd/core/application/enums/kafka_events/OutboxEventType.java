package ru.tdd.core.application.enums.kafka_events;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Tribushko Danil
 * @since 13.03.2026
 * Тип события кафки
 */
public interface OutboxEventType {

    String getType();

    static <T extends Enum<T> & OutboxEventType> T valueOf(String str, Class<T> v) {
        return Arrays.stream(v.getEnumConstants()).
                filter(e -> Objects.equals(e.getType(), str))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(v.getName() + " неверный формат: " + str));
    }
}
