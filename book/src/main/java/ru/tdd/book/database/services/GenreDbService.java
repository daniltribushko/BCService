package ru.tdd.book.database.services;

import ru.tdd.book.database.entities.Genre;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Сервис для работы с жанрами в бд
 */
public interface GenreDbService {

    /**
     * Получение по идентификатору с выбрасыванием исключения, если объект не найден
     */
    Genre getById(UUID id);
}
