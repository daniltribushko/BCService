package ru.tdd.bc.book.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.bc.book.database.entities.Publisher;

import java.util.UUID;

/**
 * @author Tribushko 31.07.2026
 * Репозиторий для работы с издателями
 */
@Repository
public interface PublisherRepository extends JpaRepository<Publisher, UUID>, JpaSpecificationExecutor<Publisher> {
}
