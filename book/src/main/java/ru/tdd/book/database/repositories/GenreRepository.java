package ru.tdd.book.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.book.database.entities.Genre;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Репозитоий для работ с жанрами книг
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID>, JpaSpecificationExecutor<Genre> {
}
