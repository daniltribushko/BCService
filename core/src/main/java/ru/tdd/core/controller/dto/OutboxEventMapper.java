package ru.tdd.core.controller.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.tdd.core.database.entities.kafka.OutboxEvent;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OutboxEventMapper {

    OutboxEventDTO toDto(OutboxEvent entity);
}
