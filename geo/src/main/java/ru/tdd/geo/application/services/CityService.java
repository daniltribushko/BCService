package ru.tdd.geo.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.geo.application.models.dto.geo.city.*;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 13.01.2026
 * Сервис для работы с городами
 */
@Transactional(readOnly = true)
public interface CityService {

    @Transactional
    CityDTO create(CreateCityDTO dto);

    @Transactional
    CityDTO update(UUID id, UpdateCityDTO dto);

    CityDetailsDTO getById(UUID id);

    @Transactional
    void delete(UUID id);

    CitiesDTO getAll(
            String name,
            String regionName,
            String countryName,
            int page,
            int perPage
    );
}
