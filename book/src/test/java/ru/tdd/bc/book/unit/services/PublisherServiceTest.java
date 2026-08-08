package ru.tdd.bc.book.unit.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.UpdatePublisherDTO;
import ru.tdd.bc.book.application.exceptions.PublisherAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.PublisherByIdNotFoundException;
import ru.tdd.bc.book.application.mappers.PublisherMapper;
import ru.tdd.bc.book.application.redis.CountryRedisService;
import ru.tdd.bc.book.application.services.imp.PublisherServiceImp;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.database.entities.Publisher;
import ru.tdd.bc.book.database.repositories.PublisherRepository;
import ru.tdd.bc.book.database.service.PublisherDbService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 06.08.2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit тест сервиса по работе с издателями")
public class PublisherServiceTest {

    @Mock
    private CountryRedisService countryService;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private PublisherMapper publisherMapper;

    @Mock
    private PublisherDbService publisherDbService;

    @InjectMocks
    private PublisherServiceImp publisherService;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        UUID publisherId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Mockito.when(publisherRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(publisherMapper.toEntity(any(CreatePublisherDTO.class))).thenReturn(
                Publisher.builder()
                        .id(publisherId)
                        .name("Вече")
                        .url("https://veche.ru")
                        .build()
        );
        Mockito.when(countryService.get(countryId)).thenReturn(new Country(countryId, "Россия"));
        Mockito.when(publisherMapper.toDto(any(Publisher.class))).thenReturn(
                new PublisherDTO(
                        publisherId,
                        "Вече",
                        "https://veche.ru",
                        new CountryDTO(countryId, null),
                        null,
                        null
                )
        );

        PublisherDTO actual = publisherService.create(new CreatePublisherDTO(null, null, countryId));

        Mockito.verify(publisherRepository).save(any(Publisher.class));
        Mockito.verify(publisherMapper).toEntity(any(CreatePublisherDTO.class));
        Mockito.verify(countryService).get(countryId);
        Mockito.verify(publisherMapper).toDto(any(Publisher.class));

        Assertions.assertEquals(publisherId, actual.getId());
        Assertions.assertEquals("Вече", actual.getName());
        Assertions.assertEquals("https://veche.ru", actual.getUrl());
        Assertions.assertEquals(countryId, actual.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное создание - издатель уже создан")
    void saveAlreadyExistsFailTest() {
        UUID publisherId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Mockito.when(publisherMapper.toEntity(any(CreatePublisherDTO.class))).thenReturn(
                Publisher.builder()
                        .id(publisherId)
                        .name("Вече")
                        .url("https://veche.ru")
                        .country(new Country(countryId, null))
                        .build()
        );
        Mockito.when(publisherRepository.exists(any(Specification.class))).thenReturn(true);

        PublisherAlreadyExistsException actual = Assertions.assertThrows(
                PublisherAlreadyExistsException.class,
                () -> publisherService.create(new CreatePublisherDTO("Вече", "https://veche.ru", countryId))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(PublisherAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        UUID publisherId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();
        UUID countryId2 = UUID.randomUUID();

        Country country = new Country(countryId, "Россия");

        Mockito.when(publisherDbService.getById(publisherId)).thenReturn(
                Publisher.builder()
                        .id(publisherId)
                        .name("Вече")
                        .country(country)
                        .build()
        );
        Mockito.when(publisherRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(countryService.get(countryId2)).thenReturn(country);
        Mockito.when(publisherMapper.toDto(any(Publisher.class))).thenReturn(
                new PublisherDTO(
                        publisherId,
                        "Вече",
                        null,
                        new CountryDTO(countryId2, "Россия"),
                        null,
                        null
                )
        );

        PublisherDTO actual = publisherService.update(publisherId, new UpdatePublisherDTO(null, null, countryId2));

        Mockito.verify(publisherDbService).getById(publisherId);
        Mockito.verify(publisherRepository).exists(any(Specification.class));
        Mockito.verify(countryService).get(countryId2);
        Mockito.verify(publisherMapper).toDto(any(Publisher.class));

        Assertions.assertEquals(publisherId, actual.getId());
        Assertions.assertEquals("Вече", actual.getName());
        Assertions.assertNull(actual.getUrl());
        Assertions.assertEquals(countryId2, actual.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - издатель не найден")
    void updateNotFoundFailTest() {
        UUID publisherId = UUID.randomUUID();

        Mockito.when(publisherDbService.getById(publisherId)).thenThrow(new PublisherByIdNotFoundException());

        PublisherByIdNotFoundException actual = Assertions.assertThrows(
                PublisherByIdNotFoundException.class,
                () -> publisherService.update(publisherId, new UpdatePublisherDTO())
        );

        Mockito.verify(publisherDbService).getById(publisherId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - издатель уже создан")
    void updateAlreadyExistsServiceTest() {
        UUID publisherId = UUID.randomUUID();

        Mockito.when(publisherDbService.getById(publisherId)).thenReturn(
                Publisher.builder()
                        .id(publisherId)
                        .name("Вече")
                        .country(new Country(UUID.randomUUID(), ""))
                        .build()
        );
        Mockito.when(publisherRepository.exists(any(Specification.class))).thenReturn(true);

        PublisherAlreadyExistsException actual = Assertions.assertThrows(
                PublisherAlreadyExistsException.class,
                () -> publisherService.update(publisherId, new UpdatePublisherDTO())
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(PublisherAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        UUID publisherId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Mockito.when(publisherDbService.getById(publisherId)).thenReturn(
                Publisher.builder()
                        .id(publisherId)
                        .name("Вече")
                        .country(new Country(countryId, "Россия"))
                        .build()
        );
        Mockito.when(publisherMapper.toDto(any(Publisher.class)))
                .thenReturn(
                        new PublisherDTO(
                                publisherId,
                                "Вече",
                                null,
                                new CountryDTO(countryId, "Россия"),
                                null,
                                null
                        )
                );

        PublisherDTO actual = publisherService.getById(publisherId);

        Mockito.verify(publisherDbService).getById(publisherId);
        Mockito.verify(publisherMapper).toDto(any(Publisher.class));

        Assertions.assertEquals(publisherId, actual.getId());
        Assertions.assertEquals("Вече", actual.getName());
        Assertions.assertEquals(countryId, actual.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - издатель не найден")
    void getByIdNotFoundFailTest() {
        UUID publisherId = UUID.randomUUID();
        Mockito.when(publisherDbService.getById(publisherId)).thenThrow(new PublisherByIdNotFoundException());

        PublisherByIdNotFoundException actual = Assertions.assertThrows(
                PublisherByIdNotFoundException.class,
                () -> publisherService.getById(publisherId)
        );

        Mockito.verify(publisherDbService).getById(publisherId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        UUID publisherId = UUID.randomUUID();

        Mockito.when(publisherDbService.getById(publisherId)).thenReturn(
                Publisher.builder()
                        .id(publisherId)
                        .build()
        );

        publisherService.delete(publisherId);

        Mockito.verify(publisherDbService).getById(publisherId);
        Mockito.verify(publisherRepository).delete(any(Publisher.class));
    }

    @Test
    @DisplayName("Неудачное удаление - издатель не найден")
    void deleteNotFoundFailTest() {
        UUID publisherId = UUID.randomUUID();

        Mockito.when(publisherDbService.getById(publisherId)).thenThrow(new PublisherByIdNotFoundException());

        PublisherByIdNotFoundException actual = Assertions.assertThrows(
                PublisherByIdNotFoundException.class,
                () -> publisherService.delete(publisherId)
        );

        Mockito.verify(publisherDbService).getById(publisherId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), actual.getMessage());
    }
}
