package ru.tdd.bc.book.database.service;

import ru.tdd.bc.book.database.entities.Genre;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 * Сервис для работы с жанрами
 */
public interface GenreDbService {

    Genre getById(UUID id);
}
