package ru.tdd.author.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.author.database.entitites.Author;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Репаозиторий для работы с авторами
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID>, JpaSpecificationExecutor<Author> {
}
