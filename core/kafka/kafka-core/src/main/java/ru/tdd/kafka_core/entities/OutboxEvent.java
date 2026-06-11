package ru.tdd.kafka_core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import ru.tdd.bc.database.entity.BaseEntity;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Событие дял отправки в брокер сообщений
 */
@Entity
@Table(name = "outbox_event")
public class OutboxEvent extends BaseEntity {

    @NotNull
    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @NotEmpty
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OutboxEventType type;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "protocol_version", nullable = false)
    private int protocolVersion;

    public OutboxEvent() {}

    public OutboxEvent(Class<?> entityClass, byte[] data, OutboxEventType type, int protocolVersion) {
        this.entityName = entityClass.getName();
        this.data = data;
        this.type = type;
        this.protocolVersion = protocolVersion;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(Class<?> entityClass) {
        this.entityName = entityClass.getName();
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
