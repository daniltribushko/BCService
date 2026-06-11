package ru.tdd.bc.dictionaries.services;

import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionariesListDataDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Сервис для работы со справочниками
 */
public interface DictionaryService {

    DictionaryDto create(CreateDictionaryDto dto);

    DictionaryDto update(UUID id, DictionaryDto dto);

    DictionaryDto getById(UUID id);

    DictionariesListDataDto getAll(String name, int page, int perPage);
}
