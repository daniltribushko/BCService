package ru.tdd.core.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tdd.core.database.entities.kafka.OutboxEvent;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.03.2026
 * Репозиторий для работы с событиями kafka
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findAllByEntityName(String entityName);
}
