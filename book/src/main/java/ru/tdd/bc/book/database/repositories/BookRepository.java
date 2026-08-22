package ru.tdd.bc.book.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.bc.book.database.entities.Book;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 15.08.2026
 * Репозиторий для работы с книгами
 */
@Repository
public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
}
