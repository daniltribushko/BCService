package ru.tdd.kafka_core.entities;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Тип события для отправки в брокер сообщений
 */
public enum OutboxEventType {
    /** Создание объекта */
    CREATE,
    /** Удаление объекта */
    DELETE,
    /** Обновление объекта */
    UPDATE
}
