package ru.tdd.geo.application.services;


import ru.tdd.geo.application.models.dto.geo.country.*;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * Сервис для работы со странами
 */
public interface CountryService {

    CountryDTO create(CreateCountryDTO createDTO);

    CountryDTO update(UUID id, UpdateCountryDTO updateDTO);

    void delete(UUID id);

    CountryDetailsDTO getById(UUID id);

    CountriesDTO getAll(String name, int page, int perPage);
}
