package ru.tdd.geo.application.services;

import ru.tdd.geo.application.models.enums.event.OutboxEventType;
import ru.tdd.geo.database.entities.BaseEntity;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Сервис для отправки данных в kafka
 */
public interface KafkaService<E extends OutboxEventType, T extends BaseEntity> {

    void send(E type, T entity);
}
