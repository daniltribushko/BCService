package ru.tdd.bc.book.application.services;

import ru.tdd.bc.book.application.dto.publisher.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 01.08.2026
 * Сервис для работы с издателями
 */
public interface PublisherService {

    PublisherDTO create(CreatePublisherDTO dto);

    PublisherDTO update(UUID id, UpdatePublisherDTO dto);

    PublisherDTO getById(UUID id);

    void delete(UUID id);

    PublisherListDTO getAll(
            String name,
            String countryName,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            int page,
            int perPage
    );
}
