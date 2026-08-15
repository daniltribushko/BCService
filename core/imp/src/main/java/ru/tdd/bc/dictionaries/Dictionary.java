package ru.tdd.bc.dictionaries;

import ru.tdd.bc.dictionaries.entities.NameEntity;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Модель справочника
 */
public interface Dictionary extends NameEntity {

    UUID getId();

    void setId(UUID id);

    String getName();

    void  setName(String name);
}
