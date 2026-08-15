package ru.tdd.bc.book.database.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tdd.bc.book.application.exceptions.GenreByIdNotFoundException;
import ru.tdd.bc.book.database.entities.Genre;
import ru.tdd.bc.book.database.repositories.GenreRepository;
import ru.tdd.bc.book.database.service.GenreDbService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 */
@Service
public class GenreDbServiceImp implements GenreDbService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreDbServiceImp(
            GenreRepository genreRepository
    ) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre getById(UUID id) {
        return genreRepository.findById(id).orElseThrow(GenreByIdNotFoundException::new);
    }
}
