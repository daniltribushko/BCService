package ru.tdd.author.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tdd.author.database.entitites.OutboxEvent;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
 * Репозиторий для работы с событиями kafka
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}
