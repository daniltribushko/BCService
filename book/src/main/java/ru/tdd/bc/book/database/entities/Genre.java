package ru.tdd.bc.book.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.tdd.bc.database.entity.BaseEntity;
import ru.tdd.bc.database.validators.annotations.NotBlankSize;
import ru.tdd.bc.dictionaries.Dictionary;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 * Жанр книг
 */
@Entity
@Table(name = "genre")
public class Genre extends BaseEntity implements Dictionary {

    @NotBlankSize
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

    public Genre(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
