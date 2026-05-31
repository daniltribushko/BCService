package ru.tdd.book.database.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tdd.book.database.entities.Genre;
import ru.tdd.book.database.repositories.GenreRepository;
import ru.tdd.book.domain.exceptions.genre.GenreByIdNotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 */
@Service
public class GenreDbServiceImp implements GenreDbService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreDbServiceImp(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre getById(UUID id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new GenreByIdNotFoundException(id));
    }
}
