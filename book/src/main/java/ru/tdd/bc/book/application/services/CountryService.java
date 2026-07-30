package ru.tdd.bc.book.application.services;

import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.countries.CountryListDTO;

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
