package ru.tdd.author.application.mappers;

import org.mapstruct.Mapper;
import ru.tdd.author.application.dto.OutboxEventDTO;
import ru.tdd.author.database.entitites.OutboxEvent;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
 * Маппер собый kafka
 */
@Mapper(componentModel = "spring")
public interface OutboxEventMapper {

    OutboxEventDTO toDto(OutboxEvent outboxEvent);

    OutboxEvent toEntity(OutboxEventDTO outboxEventDTO);
}
