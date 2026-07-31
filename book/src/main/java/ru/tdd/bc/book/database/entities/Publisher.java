package ru.tdd.bc.book.database.entities;

import jakarta.persistence.*;
import ru.tdd.bc.database.entity.EntityVersion;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 31.07.2026
 * <p>
 * Издатель книги
 */
@Entity
@Table(name = "publisher")
public class Publisher extends EntityVersion {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country")
    private Country country;

    @Column(name = "url", nullable = false)
    private String url;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID id;

        private String name;

        private Country country;

        private String url;

        private LocalDateTime updateTime;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder country(Country country) {
            this.country = country;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Publisher build() {
            Publisher publisher = new Publisher();
            publisher.id = this.id;
            publisher.name = this.name;
            publisher.url = this.url;
            publisher.country = this.country;
            publisher.updateTime = this.updateTime;

            return publisher;
        }
    }

    public Publisher() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
