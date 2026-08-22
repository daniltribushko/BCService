package ru.tdd.bc.book.database.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.ISBN;
import ru.tdd.bc.database.entity.EntityVersion;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 15.08.2026
 * Сущность книги
 */
@Entity
@Table(name = "book")
public class Book extends EntityVersion {

    @Size(max = 255)
    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 13)
    @ISBN(type = ISBN.Type.ANY)
    @NotBlank
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @NotNull
    @Past(message = "Год издания книги, должен быть в прошедшем времени")
    @Column(name = "publishing_year", nullable = false)
    private Integer publishingYear;

    @Column(name = "total_pages", nullable = false)
    @Min(value = 1, message = "Количество страниц должно быть больше 0")
    private int totalPages;

    @Size(max = 3000)
    @Column(name = "description", length = 3000)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "author_id"}),
            name = "books_2_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>(1);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "genre_id"}),
            name = "books_2_genres",
            joinColumns = @JoinColumn(name = "book_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "genre_id", nullable = false)
    )
    private Set<Genre> genres = new HashSet<>(1);

    public static class Builder {

        private UUID id;

        private String title;

        private String isbn;

        private Publisher publisher;

        private Integer publishingYear;

        private int totalPages;

        private String description;

        private Set<Author> authors = new HashSet<>(1);

        private Set<Genre> genres = new HashSet<>(1);

        private LocalDateTime updateTime;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder publisher(Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder publishingYear(Integer publishingYear) {
            this.publishingYear = publishingYear;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder authors(Set<Author> authors) {
            this.authors = authors;
            return this;
        }

        public Builder genres(Set<Genre> genres) {
            this.genres = genres;
            return this;
        }

        public Builder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Book build() {
            Book book = new Book();

            book.id = this.id;
            book.title = this.title;
            book.isbn = this.isbn;
            book.publisher = this.publisher;
            book.publishingYear = this.publishingYear;
            book.totalPages = this.totalPages;
            book.description = this.description;
            book.authors = this.authors;
            book.genres = this.genres;
            book.updateTime = this.updateTime;

            return book;
        }
    }
}
