package ru.tdd.geo.application.services;

import ru.tdd.geo.application.models.dto.geo.region.*;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Сервис для работы с регионами
 */
public interface RegionService {

    /**
     * Создание региона
     */
    RegionDTO create(CreateRegionDTO dto);

    /**
     * Обновление региона
     */
    RegionDTO update(UUID id, UpdateRegionDTO dto);

    /**
     * Удаление региона
     */
    void delete(UUID id);

    /**
     * Получение региона по идентификатору
     */
    RegionDetailsDTO getById(UUID id);

    /**
     * Получение списка регионов с фильтрами
     */
    RegionsDTO getAll(String name, String countryName, int page, int perPage);
}
