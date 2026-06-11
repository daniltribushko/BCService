package ru.tdd.kafka_core.dto;

import ru.tdd.kafka_core.entities.OutboxEventType;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * DTO события кафки
 */
public class OutboxEventDto {

    private String entityName;

    private byte[] data;

    private OutboxEventType type;

    private int protocolVersion;

    public OutboxEventDto(String entityName, byte[] data, OutboxEventType type, int protocolVersion) {
        this.entityName = entityName;
        this.data = data;
        this.type = type;
        this.protocolVersion = protocolVersion;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public OutboxEventType getType() {
        return type;
    }

    public void setType(OutboxEventType type) {
        this.type = type;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}
