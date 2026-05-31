package ru.tdd.book.controllers.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.GenreListDataDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.book.domain.services.genres.GenreService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 */
@RestController
public class GenreControllerImp implements GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreControllerImp(
            GenreService genreService
    ) {
        this.genreService = genreService;
    }

    @Override
    public GenreDto create(CreateGenreDto dto) {
        return genreService.create(dto);
    }

    @Override
    public GenreDto update(UUID id, UpdateGenreDto dto) {
        return genreService.update(id, dto);
    }

    @Override
    public GenreDto getById(UUID id) {
        return genreService.getById(id);
    }

    @Override
    public void delete(UUID id) {
        genreService.delete(id);
    }

    @Override
    public GenreListDataDto getAll(String name, int page, int perPage) {
        return genreService.getAll(name, page, perPage);
    }
}
