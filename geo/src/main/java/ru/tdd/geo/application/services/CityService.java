package ru.tdd.geo.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.geo.application.models.dto.geo.city.*;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 13.01.2026
 * Сервис для работы с городами
 */
public interface CityService {

    CityDTO create(CreateCityDTO dto);

    CityDTO update(UUID id, UpdateCityDTO dto);

    CityDetailsDTO getById(UUID id);

    void delete(UUID id);

    CityListData getAll(
            String name,
            String regionName,
            String countryName,
            int page,
            int perPage
    );
}
