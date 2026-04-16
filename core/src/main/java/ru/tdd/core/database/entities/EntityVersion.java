package ru.tdd.core.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 14.03.2026
 * Сущность с временем создания и последним обновлением
 */
@MappedSuperclass
public abstract class EntityVersion extends BaseEntity {

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
