package ru.tdd.geo.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tdd.geo.database.entities.OutboxEvent;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Репозиторий для работы с событиями kafka
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findAllByEntityName(String entityName);
}
