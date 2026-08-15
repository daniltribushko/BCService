package ru.tdd.bc.book.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.bc.book.application.services.GenreService;
import ru.tdd.bc.book.controller.rest_controllers.GenreController;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionariesListDataDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.08.2026
 */
@RestController
public class GenreControllerImp implements GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreControllerImp(GenreService genreService) {
        this.genreService = genreService;
    }

    @Override
    public ResponseEntity<DictionaryDto> create(CreateDictionaryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(dto));
    }

    @Override
    public ResponseEntity<DictionaryDto> update(UUID genreId, UpdateDictionaryDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(genreService.update(genreId, dto));
    }

    @Override
    public ResponseEntity<?> delete(UUID genreId) {
        genreService.delete(genreId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<DictionaryDto> getById(UUID genreId) {
        return ResponseEntity.status(HttpStatus.OK).body(genreService.getById(genreId));
    }

    @Override
    public ResponseEntity<DictionariesListDataDto> getAll(String name, int page, int perPage) {
        return ResponseEntity.status(HttpStatus.OK).body(genreService.getAll(name, page, perPage));
    }
}
