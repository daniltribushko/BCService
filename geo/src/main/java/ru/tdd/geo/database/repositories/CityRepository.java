package ru.tdd.geo.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.geo.database.entities.City;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Репозиторий для работы с городами
 */
@Repository
public interface CityRepository extends JpaSpecificationExecutor<City>, JpaRepository<City, UUID> {
}
