package ru.tdd.bc.book.database.service;

import ru.tdd.bc.book.database.entities.Country;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Сервис для работы с моделями-дб стран
 */
public interface CountryDbService {

    Country getById(UUID id);
}
