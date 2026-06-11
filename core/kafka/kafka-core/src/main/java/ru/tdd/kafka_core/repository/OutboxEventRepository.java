package ru.tdd.kafka_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tdd.kafka_core.entities.OutboxEvent;

import java.util.Set;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Репозиторий для работы с событиями
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    Set<OutboxEvent> findAllByEntityName(String entityName);
}
