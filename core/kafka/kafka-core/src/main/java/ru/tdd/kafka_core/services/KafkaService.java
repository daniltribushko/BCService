package ru.tdd.kafka_core.services;

import ru.tdd.bc.database.entity.BaseEntity;
import ru.tdd.kafka_core.entities.OutboxEventType;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Сервис для отправки данных в kafka
 */
public interface KafkaService<E extends OutboxEventType, T extends BaseEntity> {

    void send(E type, T entity);
}