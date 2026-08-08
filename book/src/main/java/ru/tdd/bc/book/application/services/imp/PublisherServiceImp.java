package ru.tdd.bc.book.application.services.imp;

import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherListDTO;
import ru.tdd.bc.book.application.dto.publisher.UpdatePublisherDTO;
import ru.tdd.bc.book.application.exceptions.PublisherAlreadyExistsException;
import ru.tdd.bc.book.application.mappers.PublisherMapper;
import ru.tdd.bc.book.application.redis.CountryRedisService;
import ru.tdd.bc.book.application.services.PublisherService;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.database.entities.Publisher;
import ru.tdd.bc.book.database.repositories.PublisherRepository;
import ru.tdd.bc.book.database.service.PublisherDbService;
import ru.tdd.bc.book.database.specifications.PublisherSpecification;
import ru.tdd.bc.utils.TextUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.08.2026
 */
@Service
@Transactional(readOnly = true)
public class PublisherServiceImp implements PublisherService {

    private final PublisherRepository publisherRepository;

    private final PublisherMapper publisherMapper;

    private final CountryRedisService countryRedisService;

    private final PublisherDbService publisherDbService;

    @Autowired
    public PublisherServiceImp(
            PublisherRepository publisherRepository,
            PublisherMapper publisherMapper,
            CountryRedisService countryRedisService,
            PublisherDbService publisherDbService,
            Validator validator
    ) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
        this.countryRedisService = countryRedisService;
        this.publisherDbService = publisherDbService;
    }

    @Override
    @Transactional
    public PublisherDTO create(CreatePublisherDTO dto) {
        Publisher publisher = publisherMapper.toEntity(dto);

        UUID countryId = dto.getCountryId();

        if (
                publisherRepository.exists(
                        PublisherSpecification.byNameAndCountryEqual(
                                publisher.getName(),
                                countryId
                        )
                )
        )
            throw new PublisherAlreadyExistsException();

        Country country = countryRedisService.get(countryId);
        publisher.setCountry(country);

        publisherRepository.save(publisher);

        return publisherMapper.toDto(publisher);
    }

    @Override
    @Transactional
    public PublisherDTO update(UUID id, UpdatePublisherDTO dto) {
        Publisher publisher = publisherDbService.getById(id);

        String newName = dto.getName();
        UUID newCountryId = dto.getCountryId();

        if (
                publisherRepository.exists(
                        PublisherSpecification.byNameAndCountryEqual(
                                TextUtils.isEmpty(newName) ? publisher.getName() : newName,
                                newCountryId == null ? publisher.getCountry().getId() : newCountryId,
                                id
                        )
                )
        )
            throw new PublisherAlreadyExistsException();

        if (!TextUtils.isEmpty(newName))
            publisher.setName(newName);

        publisher.setUrl(dto.getUrl());

        if (newCountryId != null) {
            Country newCounty = countryRedisService.get(newCountryId);
            publisher.setCountry(newCounty);
        }

        publisher.setUpdateTime(LocalDateTime.now());

        publisherRepository.save(publisher);

        return publisherMapper.toDto(publisher);
    }

    @Override
    public PublisherDTO getById(UUID id) {
        Publisher publisher = publisherDbService.getById(id);
        return publisherMapper.toDto(publisher);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        publisherRepository.delete(publisherDbService.getById(id));
    }

    @Override
    public PublisherListDTO getAll(
            String name,
            String countryName,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            int page,
            int perPage
    ) {
        Page<Publisher> publisherPage = publisherRepository.findAll(
                PublisherSpecification.byNameCountryNameLike(
                        name,
                        countryName,
                        creationTimeStart,
                        creationTimeEnd,
                        updateTimeStart,
                        updateTimeEnd
                ),
                PageRequest.of(page, perPage)
        );

        return PublisherListDTO.builder()
                .data(publisherMapper.toDto(publisherPage.getContent()))
                .totalCount(publisherPage.getTotalElements())
                .totalPages(publisherPage.getTotalPages())
                .page(publisherPage.getNumber())
                .count(publisherPage.getSize())
                .build();
    }
}
