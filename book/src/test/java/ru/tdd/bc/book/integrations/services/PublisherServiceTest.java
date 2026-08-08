package ru.tdd.bc.book.integrations.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.book.TestcontainersConfiguration;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.UpdatePublisherDTO;
import ru.tdd.bc.book.application.exceptions.PublisherAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.PublisherByIdNotFoundException;
import ru.tdd.bc.book.application.services.PublisherService;
import ru.tdd.bc.book.database.repositories.PublisherRepository;
import ru.tdd.bc.book.sql.InitPublishersSqlScripts;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.book.utils.PublishersUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 06.08.2026
 */
@SpringBootTest
@Testcontainers
@InitPublishersSqlScripts
@Import(TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест сервиса для работы с издателями")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PublisherServiceTest {

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        PublisherDTO dto = publisherService.create(new CreatePublisherDTO("АСТ", null, CountryUtils.COUNTRY_ID1));

        Assertions.assertNotNull(dto.getId());
        Assertions.assertEquals("АСТ", dto.getName());
        Assertions.assertNull(dto.getUrl());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, dto.getCountry().getId());
        Assertions.assertEquals(4, publisherRepository.count());
    }

    @Test
    @DisplayName("Неудачное создание - издатель уже создан")
    void saveAlreadyExistsFailTest() {
        PublisherAlreadyExistsException actual = Assertions.assertThrows(
                PublisherAlreadyExistsException.class,
                () -> publisherService.create(new CreatePublisherDTO("Вече", null, CountryUtils.COUNTRY_ID1))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(PublisherAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    private static Stream<Arguments> updateSuccessTest() {
        return Stream.of(
                arguments(
                        named("Обновление названия", PublishersUtils.PUBLISHER_ID1),
                        new UpdatePublisherDTO(
                                "АСТ",
                                "https://veche.ru",
                                null
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID1,
                                "АСТ",
                                "https://veche.ru",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление электронного адреса", PublishersUtils.PUBLISHER_ID2),
                        new UpdatePublisherDTO(
                                null,
                                "https://ast.ru/",
                                null
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID2,
                                "Питер",
                                "https://ast.ru/",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Удаление электронного адреса", PublishersUtils.PUBLISHER_ID1),
                        new UpdatePublisherDTO(
                                null,
                                null,
                                null
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID1,
                                "Вече",
                                null,
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление страны", PublishersUtils.PUBLISHER_ID3),
                        new UpdatePublisherDTO(
                                null,
                                "https://en.cnpubg.com",
                                CountryUtils.COUNTRY_ID3
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID3,
                                "China Publishing Group",
                                "https://en.cnpubg.com",
                                new CountryDTO(CountryUtils.COUNTRY_ID3, null),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Полное обновление", PublishersUtils.PUBLISHER_ID3),
                        new UpdatePublisherDTO(
                                "АСТ",
                                "https://ast.ru",
                                CountryUtils.COUNTRY_ID1
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID3,
                                "АСТ",
                                "https://ast.ru",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                null,
                                null
                        )
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное обновление")
    void updateSuccessTest(UUID id, UpdatePublisherDTO dto, PublisherDTO expected) {
        PublisherDTO actual = publisherService.update(id, dto);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getUrl(), actual.getUrl());
        Assertions.assertEquals(expected.getCountry().getId(), actual.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - издатель не найден")
    void updateNotFoundFailTest() {
        UUID publisherId = UUID.randomUUID();

        PublisherByIdNotFoundException actual = Assertions.assertThrows(
                PublisherByIdNotFoundException.class,
                () -> publisherService.update(publisherId, new UpdatePublisherDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - издатель уже создан")
    void updateAlreadyExistsFailTest() {
        PublisherAlreadyExistsException actual = Assertions.assertThrows(
                PublisherAlreadyExistsException.class,
                () -> publisherService.update(
                        PublishersUtils.PUBLISHER_ID3,
                        new UpdatePublisherDTO(
                                "Питер",
                                null,
                                CountryUtils.COUNTRY_ID1
                        )
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(PublisherAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        PublisherDTO publisher = publisherService.getById(PublishersUtils.PUBLISHER_ID1);

        Assertions.assertEquals(PublishersUtils.PUBLISHER_ID1, publisher.getId());
        Assertions.assertEquals("Вече", publisher.getName());
        Assertions.assertEquals("https://veche.ru", publisher.getUrl());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, publisher.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - издатель не найден")
    void getByIdNotFoundFailTest() {
        PublisherByIdNotFoundException actual = Assertions.assertThrows(
                PublisherByIdNotFoundException.class,
                () -> publisherService.getById(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        publisherService.delete(PublishersUtils.PUBLISHER_ID1);

        Assertions.assertEquals(2, publisherRepository.count());
    }

    @Test
    @DisplayName("Неудачное удаление - пользователь не найден")
    void deleteNotFoundFailTest() {
        PublisherByIdNotFoundException actual = Assertions.assertThrows(
                PublisherByIdNotFoundException.class,
                () -> publisherService.delete(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), actual.getMessage());
    }
}
