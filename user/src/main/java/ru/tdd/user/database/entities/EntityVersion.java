package ru.tdd.user.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 29.01.2026
 * Класс с отслеживанием времени создания и обновления
 */
@MappedSuperclass
public abstract class EntityVersion extends BaseEntity {

    @Column(name = "creation_time", nullable = false)
    protected LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "update_time", nullable = false)
    protected LocalDateTime updateTime = LocalDateTime.now();

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
