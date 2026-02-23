package ru.tdd.author.database.service;

import ru.tdd.author.database.entitites.Country;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Сервис для работы с моделями-дб стран
 */
public interface CountryDbService {

    Country getById(UUID id);

    void checkCountryExists(UUID id);
}
