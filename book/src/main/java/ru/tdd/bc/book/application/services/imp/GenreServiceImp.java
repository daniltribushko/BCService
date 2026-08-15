package ru.tdd.bc.book.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.tdd.bc.book.application.exceptions.GenreAlreadyExistsException;
import ru.tdd.bc.book.application.mappers.GenreMapper;
import ru.tdd.bc.book.application.services.GenreService;
import ru.tdd.bc.book.database.entities.Genre;
import ru.tdd.bc.book.database.repositories.GenreRepository;
import ru.tdd.bc.book.database.service.GenreDbService;
import ru.tdd.bc.database.specifications.NameSpecification;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionariesListDataDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;
import ru.tdd.bc.utils.TextUtils;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 */
@Service
public class GenreServiceImp implements GenreService {

    private final GenreMapper genreMapper;

    private final GenreRepository genreRepository;

    private final GenreDbService genreDbService;

    @Autowired
    public GenreServiceImp(
            GenreMapper genreMapper,
            GenreRepository genreRepository,
            GenreDbService genreDbService
    ) {
        this.genreMapper = genreMapper;
        this.genreRepository = genreRepository;
        this.genreDbService = genreDbService;
    }

    @Override
    public DictionaryDto create(CreateDictionaryDto dto) {
        Genre genre = genreMapper.toEntity(dto);

        if (genreRepository.exists(NameSpecification.byNameEqual(genre.getName())))
            throw new GenreAlreadyExistsException();

        genreRepository.save(genre);
        return genreMapper.toDto(genre);
    }

    @Override
    public DictionaryDto update(UUID id, UpdateDictionaryDto dto) {
        Genre genre = genreDbService.getById(id);
        String newName = dto.getName();

        if (!TextUtils.isEmpty(newName)) {
            if (genreRepository.exists(NameSpecification.byNameEqual(newName)))
                throw new GenreAlreadyExistsException();
            else
                genre.setName(newName);
        }

        genreRepository.save(genre);

        return genreMapper.toDto(genre);
    }

    @Override
    public DictionaryDto getById(UUID id) {
        Genre genre = genreDbService.getById(id);
        return genreMapper.toDto(genre);
    }

    @Override
    public void delete(UUID id) {
        Genre genre = genreDbService.getById(id);
        genreRepository.delete(genre);
    }

    @Override
    public DictionariesListDataDto getAll(String name, int page, int perPage) {
        Page<Genre> genrePage = genreRepository.findAll(
                NameSpecification.byNameWithFullTextSearch(name),
                PageRequest.of(page, perPage)
        );

        return DictionariesListDataDto.builder()
                .data(genreMapper.toDto(genrePage.toList()))
                .totalPages(genrePage.getTotalPages())
                .totalCount(genrePage.getTotalElements())
                .count(genrePage.getSize())
                .page(genrePage.getNumber())
                .build();
    }
}
