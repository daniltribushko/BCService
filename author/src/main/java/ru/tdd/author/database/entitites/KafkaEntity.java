package ru.tdd.author.database.entitites;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
 * Сущность, которая пприходит из кафки
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
