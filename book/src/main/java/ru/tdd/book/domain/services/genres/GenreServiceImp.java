package ru.tdd.book.domain.services.genres;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.GenreListDataDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.book.database.entities.Genre;
import ru.tdd.book.database.repositories.GenreRepository;
import ru.tdd.book.database.services.GenreDbService;
import ru.tdd.book.database.specifications.GenreSpecification;
import ru.tdd.book.domain.exceptions.genre.GenreAlreadyExistsException;
import ru.tdd.book.domain.mappers.GenreMapper;
import ru.tdd.core.application.utils.TextUtils;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Сервис для работы с жанрами
 */
@Service
@Transactional(readOnly = true)
public class GenreServiceImp implements GenreService {

    private final GenreMapper genreMapper;

    private final GenreDbService genreDbService;

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImp(
            GenreMapper genreMapper,
            GenreDbService genreDbService,
            GenreRepository genreRepository
    ) {
        this.genreMapper = genreMapper;
        this.genreDbService = genreDbService;
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public GenreDto create(CreateGenreDto dto) {
        String name = dto.getName();

        if (genreRepository.exists(GenreSpecification.byName(name)))
            throw new GenreAlreadyExistsException(name);

        Genre genre = genreRepository.save(
                new Genre(name)
        );

        return genreMapper.toDto(genre);
    }

    @Override
    @Transactional
    public GenreDto update(UUID id, UpdateGenreDto dto) {
        Genre genre = genreDbService.getById(id);

        String name = dto.getName();

        if (!TextUtils.isEmpty(name)) {
            if (genreRepository.exists(GenreSpecification.byName(name)))
                throw new GenreAlreadyExistsException(name);

            genreMapper.update(dto, genre);
        }


        genreRepository.save(genre);

        return genreMapper.toDto(genre);
    }

    @Override
    public GenreDto getById(UUID id) {
        return genreMapper.toDto(genreDbService.getById(id));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        genreRepository.delete(genreDbService.getById(id));
    }

    @Override
    public GenreListDataDto getAll(String name, int page, int perPage) {
        return new GenreListDataDto(
                genreRepository.findAll(
                                GenreSpecification.byName(name),
                                PageRequest.of(page, perPage)
                        ).stream()
                        .map(genreMapper::toDto)
                        .toList()
        );
    }
}
