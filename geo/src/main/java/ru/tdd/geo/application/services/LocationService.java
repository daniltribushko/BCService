package ru.tdd.geo.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationListData;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.01.2026
 * Сервис для работы с локациями
 */
@Transactional(readOnly = true)
public interface LocationService {

    @Transactional
    LocationDTO create(CreateLocationDTO dto);

    @Transactional
    LocationDTO update(UUID id, UpdateLocationDTO dto);

    LocationDTO getById(UUID id);

    @Transactional
    void delete(UUID id);

    LocationListData getAll(String name, String cityName, String regionName, String countryName, int page, int perPage);
}
