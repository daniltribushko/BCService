package ru.tdd.kafka_core.mappers;

import org.mapstruct.Mapper;
import ru.tdd.kafka_core.dto.OutboxEventDto;
import ru.tdd.kafka_core.entities.OutboxEvent;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Маппер событий outbox
 */
@Mapper(componentModel = SPRING)
public interface OutboxEventMapper {

    OutboxEventDto toDto(OutboxEvent entity);
}
