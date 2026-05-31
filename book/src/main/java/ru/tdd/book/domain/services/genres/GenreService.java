package ru.tdd.book.domain.services.genres;

import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.GenreListDataDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.core.application.services.DictionaryService;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Сервис для работы с жанрами
 */
public interface GenreService extends DictionaryService<CreateGenreDto, UpdateGenreDto, GenreDto> {

    GenreListDataDto getAll(String name, int page, int perPage);
}
