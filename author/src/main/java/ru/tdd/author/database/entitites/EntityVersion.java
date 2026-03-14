package ru.tdd.author.database.entitites;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Сущность с временем создания и последним обновлением
 */
@MappedSuperclass
public abstract class EntityVersion extends BaseEntity {

    protected LocalDateTime creationTime = LocalDateTime.now();

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
