package ru.tdd.user.database.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.tdd.user.application.models.enums.Role;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tribushko Danil
 * @since 29.01.2026
 * Конвертер для списка ролей
 */
@Converter
public class RoleConverter implements AttributeConverter<List<Role>, String> {
    @Override
    public String convertToDatabaseColumn(List<Role> attribute) {
        return attribute.stream()
                .map(Role::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Role> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(",")).map(Role::valueOf).toList();
    }
}
