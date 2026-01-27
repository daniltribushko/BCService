package ru.tdd.geo.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationsDTO;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.01.2026
 * Сервис для работы с локациями
 */
public interface LocationService {

    @Transactional
    LocationDTO create(CreateLocationDTO dto);

    @Transactional
    LocationDTO update(UUID id, UpdateLocationDTO dto);

    @Transactional
    LocationDTO getById(UUID id);

    @Transactional
    void delete(UUID id);

    @Transactional
    LocationsDTO getAll(String name, String cityName, int page, int perPage);
}
