package ru.tdd.author.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.author.database.entitites.Country;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Репозиторий для работы со странами
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, UUID>, JpaSpecificationExecutor<Country> {
}
