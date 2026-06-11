package ru.tdd.bc.dictionaries.controllers;

import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionariesListDataDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Контроллер для работы со справочниками
 */
public interface DictionaryController {

    DictionaryDto create(CreateDictionaryDto dto);

    DictionaryDto update(UUID id, UpdateDictionaryDto dto);

    DictionaryDto getById(UUID id);

    DictionariesListDataDto getAll(String name, int page, int perPage);
}
