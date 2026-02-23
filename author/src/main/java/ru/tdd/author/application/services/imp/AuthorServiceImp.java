package ru.tdd.author.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.tdd.author.application.dto.authors.*;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.exceptions.country.AuthorByIdNotFoundException;
import ru.tdd.author.application.mappers.AuthorMapper;
import ru.tdd.author.application.redis.CountryRedisService;
import ru.tdd.author.application.services.AuthorService;
import ru.tdd.author.application.utils.TextUtils;
import ru.tdd.author.database.entitites.Author;
import ru.tdd.author.database.repositories.AuthorRepository;
import ru.tdd.author.database.specifications.AuthorSpecification;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
@Service
public class AuthorServiceImp implements AuthorService {

    private final AuthorRepository authorRepository;

    private final CountryRedisService countryService;

    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorServiceImp(
            AuthorRepository authorRepository,
            CountryRedisService countryService,
            AuthorMapper authorMapper
    ) {
        this.authorRepository = authorRepository;
        this.countryService = countryService;
        this.authorMapper = authorMapper;
    }

    @Override
    public AuthorDTO create(CreateAuthorDTO dto) {
        UUID countryId = dto.getCountryId();

        CountryDTO country = countryService.get(countryId);

        Author author = new Author(
                dto.getLastName(),
                dto.getMiddleName(),
                dto.getFirstName(),
                countryId
        );

        authorRepository.save(author);

        return authorMapper.toDto(author, country);
    }

    @Override
    public AuthorDTO update(UUID id, UpdateAuthorDTO dto) {

        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorByIdNotFoundException(id));

        boolean isUpdate = false;

        String lastName = dto.getLastName();
        String middleName = dto.getMiddleName();
        String firstName = dto.getFirstName();
        UUID countryId = dto.getCountryId();

        if (TextUtils.isNonEmpty(lastName) && !Objects.equals(author.getLastName(), lastName)) {
            author.setLastName(lastName);
            isUpdate = true;
        }

        if (!Objects.equals(author.getMiddleName(), middleName)) {
            author.setMiddleName(middleName);
            isUpdate = true;
        }

        if (TextUtils.isNonEmpty(firstName) && !Objects.equals(author.getFirstName(), firstName)) {
            author.setFirstName(firstName);
            isUpdate = true;
        }

        if (countryId != null && !Objects.equals(author.getCountry(), countryId)) {
            author.setCountry(countryId);
        }

        if (isUpdate)
            author.setUpdateTime(LocalDateTime.now());

        authorRepository.save(author);

        return authorMapper.toDto(author, countryService.get(author.getCountry()));
    }

    @Override
    public AuthorDetailsDTO getById(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorByIdNotFoundException(id));
        return authorMapper.toDetailsDto(
                author,
                countryService.get(author.getCountry())
        );
    }

    @Override
    public void delete(UUID id) {
        authorRepository.delete(
                authorRepository.findById(id)
                        .orElseThrow(() -> new AuthorByIdNotFoundException(id))
        );
    }

    @Override
    public AuthorListDTO getAll(String fio, String countryName, int page, int perPage) {
        return new AuthorListDTO(
                authorRepository.findAll(
                                AuthorSpecification.byFioAndCountryNameDate(fio, countryName),
                                PageRequest.of(page, perPage)
                        ).stream()
                        .map(author -> authorMapper.toDto(author, countryService.get(author.getCountry())))
                        .toList()
        );
    }

    @Override
    public AuthorDetailsListDTO getAllDetails(
            String fio,
            String countryName,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            int page,
            int perPage
    ) {
        return new AuthorDetailsListDTO(
                authorRepository.findAll(
                                AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                        fio,
                                        countryName,
                                        creationTimeStart,
                                        creationTimeEnd,
                                        updateTimeStart,
                                        updateTimeEnd
                                ),
                                PageRequest.of(page, perPage)
                        ).stream()
                        .map(author ->
                                authorMapper.toDetailsDto(
                                        author,
                                        countryService.get(author.getCountry())
                                )
                        )
                        .toList()
        );
    }
}
