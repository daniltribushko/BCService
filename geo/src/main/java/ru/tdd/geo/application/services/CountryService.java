package ru.tdd.geo.application.services;


import org.springframework.transaction.annotation.Transactional;
import ru.tdd.geo.application.models.dto.geo.country.*;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * Сервис для работы со странами
 */
@Transactional(readOnly = true)
public interface CountryService {

    @Transactional
    CountryDTO create(CreateCountryDTO createDTO);

    @Transactional
    CountryDTO update(UUID id, UpdateCountryDTO updateDTO);

    @Transactional
    void delete(UUID id);

    CountryDetailsDTO getById(UUID id);

    CountriesDTO getAll(String name, int page, int perPage);
}
