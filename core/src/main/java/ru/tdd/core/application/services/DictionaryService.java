package ru.tdd.core.application.services;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 03.05.2026
 * @param <C> dto на создание слравочника
 * @param <U> dto на обновление справочника
 * @param <T> dto справочника
 */
public interface DictionaryService<C, U, T> {

    T create(C dto);

    T update(UUID id, U dto);

    void delete(UUID id);

    T getById(UUID id);
}
