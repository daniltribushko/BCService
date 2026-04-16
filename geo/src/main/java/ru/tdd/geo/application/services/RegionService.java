package ru.tdd.geo.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.geo.application.models.dto.geo.region.*;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Сервис для работы с регионами
 */
@Transactional(readOnly = true)
public interface RegionService {

    /**
     * Создание региона
     */
    @Transactional
    RegionDTO create(CreateRegionDTO dto);

    /**
     * Обновление региона
     */
    @Transactional
    RegionDTO update(UUID id, UpdateRegionDTO dto);

    /**
     * Удаление региона
     */
    @Transactional
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
