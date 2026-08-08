package ru.tdd.bc.book.database.service;

import ru.tdd.bc.book.database.entities.Publisher;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.08.2026
 * Сервис для работы с издателями в бд
 */
public interface PublisherDbService {

    Publisher getById(UUID id);
}
