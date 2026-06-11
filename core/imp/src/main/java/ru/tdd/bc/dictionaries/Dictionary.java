package ru.tdd.bc.dictionaries;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Модель справочника
 */
public interface Dictionary {

    UUID getId();

    void setId(UUID id);

    String getName();

    void  setName(String name);
}
