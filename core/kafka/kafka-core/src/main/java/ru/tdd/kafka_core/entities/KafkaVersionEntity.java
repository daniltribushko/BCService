package ru.tdd.kafka_core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class KafkaVersionEntity extends KafkaEntity {

    @NotNull
    @Column(name = "creation_time", nullable = false)
    protected final LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "update_time")
    protected LocalDateTime updateTime = LocalDateTime.now();

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
