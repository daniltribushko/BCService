package ru.tdd.bc.book.database.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tdd.bc.book.application.exceptions.PublisherByIdNotFoundException;
import ru.tdd.bc.book.database.entities.Publisher;
import ru.tdd.bc.book.database.repositories.PublisherRepository;
import ru.tdd.bc.book.database.service.PublisherDbService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 06.08.2026
 */
@Service
public class PublisherDbServiceImp implements PublisherDbService {

    private final PublisherRepository publisherRepository;

    @Autowired
    public PublisherDbServiceImp(
            PublisherRepository publisherRepository
    ) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public Publisher getById(UUID id) {
        return publisherRepository.findById(id).orElseThrow(PublisherByIdNotFoundException::new);
    }
}
