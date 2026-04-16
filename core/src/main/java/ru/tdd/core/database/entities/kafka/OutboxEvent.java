package ru.tdd.core.database.entities.kafka;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import ru.tdd.core.database.entities.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent extends BaseEntity {

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @NotNull
    @Column(name = "event_type", nullable = false)
    private String eventType;

    @NotNull
    @Column(name = "payload", nullable = false)
    private String payload;

    @NotNull
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    public OutboxEvent() {}

    public OutboxEvent(String entityName, String eventType, String payload, LocalDateTime createdTime) {
        this.entityName = entityName;
        this.eventType = eventType;
        this.payload = payload;
        this.createdTime = createdTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
