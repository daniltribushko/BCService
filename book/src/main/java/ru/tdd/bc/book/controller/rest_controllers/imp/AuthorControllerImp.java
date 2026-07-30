package ru.tdd.bc.book.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.bc.book.application.dto.authors.*;
import ru.tdd.bc.book.application.services.AuthorService;
import ru.tdd.bc.book.controller.rest_controllers.AuthorController;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 */
@RestController
public class AuthorControllerImp implements AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorControllerImp(
            AuthorService authorService
    ) {
        this.authorService = authorService;
    }

    @Override
    public ResponseEntity<AuthorDTO> create(CreateAuthorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(dto));
    }

    @Override
    public ResponseEntity<AuthorDTO> update(UUID id, UpdateAuthorDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authorService.update(id, dto));
    }

    @Override
    public ResponseEntity<AuthorDetailsDTO> getById(UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(authorService.getById(id));
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AuthorListDTO> getAll(String fio, String countryName, int page, int perPage) {
        return ResponseEntity.status(HttpStatus.OK).body(authorService.getAll(fio, countryName, page, perPage));
    }

    @Override
    public ResponseEntity<AuthorDetailsListDTO> getAllDetails(
            String fio,
            String countryName,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            int page,
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        authorService.getAllDetails(
                                fio,
                                countryName,
                                creationTimeStart,
                                creationTimeEnd,
                                updateTimeStart,
                                updateTimeEnd,
                                page,
                                perPage
                        )
                );
    }
}