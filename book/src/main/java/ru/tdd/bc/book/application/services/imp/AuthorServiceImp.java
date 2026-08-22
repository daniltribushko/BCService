package ru.tdd.bc.book.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tdd.bc.book.application.dto.authors.*;
import ru.tdd.bc.book.application.exceptions.AuthorAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.AuthorByIdNotFoundException;
import ru.tdd.bc.book.application.mappers.AuthorMapper;
import ru.tdd.bc.book.application.redis.CountryRedisService;
import ru.tdd.bc.book.application.services.AuthorService;
import ru.tdd.bc.book.database.entities.Author;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.database.repositories.AuthorRepository;
import ru.tdd.bc.book.database.specifications.AuthorSpecification;
import ru.tdd.bc.utils.TextUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
@Service
@Transactional(readOnly = true)
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
    @Transactional
    public AuthorDTO create(CreateAuthorDTO dto) {
        UUID countryId = dto.getCountryId();

        Country country = countryService.get(countryId);

        Author author = authorMapper.toEntity(dto);
        author.setCountry(country);

        if (
                authorRepository.exists(
                        AuthorSpecification.byLastNameFirstNameBirthdayCountryId(
                                author.getLastName(),
                                author.getFirstName(),
                                author.getBirthday(),
                                countryId
                        )
                )
        )
            throw new AuthorAlreadyExistsException();

        authorRepository.save(author);

        return authorMapper.toDto(author);
    }

    @Override
    @Transactional
    public AuthorDTO update(UUID id, UpdateAuthorDTO dto) {

        Author author = authorRepository.findById(id).orElseThrow(AuthorByIdNotFoundException::new);

        boolean isUpdate = false;

        String lastName = dto.getLastName();
        String middleName = dto.getMiddleName();
        String firstName = dto.getFirstName();
        UUID countryId = dto.getCountryId();

        if (!TextUtils.isEmpty(lastName) && !Objects.equals(author.getLastName(), lastName)) {
            author.setLastName(lastName);
            isUpdate = true;
        }

        if (!Objects.equals(middleName, "") && !Objects.equals(author.getMiddleName(), middleName)) {
            author.setMiddleName(middleName);
            isUpdate = true;
        }

        if (!TextUtils.isEmpty(firstName) && !Objects.equals(author.getFirstName(), firstName)) {
            author.setFirstName(firstName);
            isUpdate = true;
        }

        if (countryId != null && !Objects.equals(author.getCountry().getId(), countryId)) {
            author.setCountry(countryService.get(countryId));
        }

        if (isUpdate)
            author.setUpdateTime(LocalDateTime.now());

        authorRepository.save(author);

        return authorMapper.toDto(author);
    }

    @Override
    public AuthorDTO getById(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(AuthorByIdNotFoundException::new);
        return authorMapper.toDetailsDto(author);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        authorRepository.delete(
                authorRepository.findById(id)
                        .orElseThrow(AuthorByIdNotFoundException::new)
        );
    }

    @Override
    public AuthorListDTO getAll(
            String fio,
            String countryName,
            LocalDate startBirthday,
            LocalDate endBirthday,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            int page,
            int perPage
    ) {
        Page<Author> authorPage = authorRepository.findAll(
                AuthorSpecification.byFioAndCountryNameAndBirthdayAndVersionsDate(
                        fio,
                        countryName,
                        startBirthday,
                        endBirthday,
                        creationTimeStart,
                        creationTimeEnd,
                        updateTimeStart,
                        updateTimeEnd
                ),
                PageRequest.of(page, perPage)
        );

        return AuthorListDTO.builder()
                .data(
                        authorPage.stream()
                                .map(authorMapper::toDetailsDto)
                                .toList()
                )
                .page(authorPage.getNumber())
                .count(authorPage.getSize())
                .totalPages(authorPage.getTotalPages())
                .totalCount(authorPage.getTotalElements())
                .build();
    }
}
