package ru.tdd.bc.book.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.tdd.bc.database.entity.NameEntity;
import ru.tdd.kafka_core.entities.KafkaEntity;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Модель страны
 */
@Entity
@Table(name = "country")
public class Country extends KafkaEntity implements NameEntity {

    @Column(name = "name")
    private String name;

    public Country() {}

    public Country(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
