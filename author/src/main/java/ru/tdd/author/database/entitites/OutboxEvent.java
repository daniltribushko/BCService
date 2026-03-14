package ru.tdd.author.database.entitites;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
 * Событие для публикации в kafka
 */
@Entity
@Table(name = "outbox_event")
public class OutboxEvent extends BaseEntity {

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    public OutboxEvent() {}

    public OutboxEvent(String eventType, String payload, LocalDateTime createdTime) {
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
