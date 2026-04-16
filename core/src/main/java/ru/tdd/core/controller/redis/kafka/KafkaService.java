package ru.tdd.core.controller.redis.kafka;

import ru.tdd.core.application.enums.kafka_events.OutboxEventType;
import ru.tdd.core.database.entities.BaseEntity;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Сервис для отправки данных в kafka
 */
public interface KafkaService<E extends OutboxEventType, T extends BaseEntity> {

    void send(E type, T entity);
}