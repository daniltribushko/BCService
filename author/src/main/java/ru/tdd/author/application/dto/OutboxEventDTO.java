package ru.tdd.author.application.dto;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
 * DTO события для публикации в kafka
 */
public class OutboxEventDTO {

    private String eventType;

    private String payload;

    private LocalDateTime createdTime = LocalDateTime.now();

    public OutboxEventDTO() {}

    public OutboxEventDTO(String eventType, String payload, LocalDateTime createdTime) {
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
