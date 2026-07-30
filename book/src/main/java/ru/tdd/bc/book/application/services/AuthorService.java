package ru.tdd.bc.book.application.services;

import ru.tdd.bc.book.application.dto.authors.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Сервис для работы с авторами
 */
public interface AuthorService {

    AuthorDTO create(CreateAuthorDTO dto);

    AuthorDTO update(UUID id, UpdateAuthorDTO dto);

    AuthorDetailsDTO getById(UUID id);

    void delete(UUID id);

    AuthorListDTO getAll(String fio, String countryName, int page, int perPage);

    AuthorDetailsListDTO getAllDetails(
            String fio,
            String countryName,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            int page,
            int perPage
    );
}
