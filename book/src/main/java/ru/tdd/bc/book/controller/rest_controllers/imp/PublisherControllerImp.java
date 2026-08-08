package ru.tdd.bc.book.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherListDTO;
import ru.tdd.bc.book.application.dto.publisher.UpdatePublisherDTO;
import ru.tdd.bc.book.application.services.PublisherService;
import ru.tdd.bc.book.controller.rest_controllers.PublisherController;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.08.2026
 */
@RestController
public class PublisherControllerImp implements PublisherController {

    private final PublisherService publisherService;

    @Autowired
    public PublisherControllerImp(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Override
    public ResponseEntity<PublisherDTO> create(CreatePublisherDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publisherService.create(dto));
    }

    @Override
    public ResponseEntity<PublisherDTO> update(UUID publisherId, UpdatePublisherDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(publisherService.update(publisherId, dto));
    }

    @Override
    public ResponseEntity<PublisherDTO> getById(UUID publisherId) {
        return ResponseEntity.status(HttpStatus.OK).body(publisherService.getById(publisherId));
    }

    @Override
    public ResponseEntity<?> delete(UUID publisherId) {
        publisherService.delete(publisherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<PublisherListDTO> getAll(
            String name,
            String countryName,
            LocalDateTime startCreationTime,
            LocalDateTime endCreationTime,
            LocalDateTime startUpdateTime,
            LocalDateTime endUpdateTime,
            int page,
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        publisherService.getAll(
                                name,
                                countryName,
                                startCreationTime,
                                endCreationTime,
                                startUpdateTime,
                                endUpdateTime,
                                page,
                                perPage
                        )
                );
    }
}
