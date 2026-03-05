package ru.tdd.geo.application.mappers;

import org.mapstruct.Mapper;
import ru.tdd.geo.application.models.dto.OutboxEventDTO;
import ru.tdd.geo.database.entities.OutboxEvent;

@Mapper(componentModel = "spring")
public interface OutboxEventMapper {

    OutboxEventDTO toDto(OutboxEvent entity);

    OutboxEvent toEntity(OutboxEventDTO dto);
}
