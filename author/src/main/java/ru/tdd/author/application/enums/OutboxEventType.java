package ru.tdd.author.application.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
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
