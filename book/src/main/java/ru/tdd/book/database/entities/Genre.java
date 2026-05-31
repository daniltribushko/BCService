package ru.tdd.book.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.tdd.core.database.entities.BaseEntity;
import ru.tdd.core.database.entities.NameEntity;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Жанр книги
 */
@Entity
@Table(name = "genre")
public class Genre extends BaseEntity implements NameEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;

        private String name;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Genre build() {
            Genre genre = new Genre();

            genre.id = this.id;
            genre.name = this.name;

            return genre;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
