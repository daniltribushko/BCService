package ru.tdd.author.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.author.application.dto.authors.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Сервис для работы с авторами
 */
public interface AuthorService {

    @Transactional
    AuthorDTO create(CreateAuthorDTO dto);

    @Transactional
    AuthorDTO update(UUID id, UpdateAuthorDTO dto);

    @Transactional
    AuthorDetailsDTO getById(UUID id);

    @Transactional
    void delete(UUID id);

    @Transactional
    AuthorListDTO getAll(String fio, String countryName, int page, int perPage);

    @Transactional
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
