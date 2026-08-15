package ru.tdd.bc.book.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.bc.book.database.entities.Genre;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 * Репозиторий для работы с жанрами
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID>, JpaSpecificationExecutor<Genre> {
}
