package ru.tdd.author.application.services;

import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.dto.countries.CountryListDTO;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Сервис для работы со странами
 */
public interface CountryService {

    CountryListDTO getAll(String name, int page, int perPage);

    CountryDTO getById(UUID id);
}
