package ru.tdd.core.database.entities.kafka;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.03.2026
 * Класс сущности получаемый из кафки
 */
@MappedSuperclass
public abstract class KafkaEntity {

    @Id
    protected UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
