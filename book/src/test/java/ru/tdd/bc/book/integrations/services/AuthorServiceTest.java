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
import ru.tdd.bc.book.application.dto.authors.AuthorDTO;
import ru.tdd.bc.book.application.dto.authors.CreateAuthorDTO;
import ru.tdd.bc.book.application.dto.authors.UpdateAuthorDTO;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.exceptions.AuthorByIdNotFoundException;
import ru.tdd.bc.book.application.services.AuthorService;
import ru.tdd.bc.book.database.repositories.AuthorRepository;
import ru.tdd.bc.book.database.repositories.CountryRepository;
import ru.tdd.bc.book.sql.InitAuthorsSqlScripts;
import ru.tdd.bc.book.utils.AuthorUtils;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Интегационные тесты сервиса по работе с авторами
 */
@SpringBootTest
@Testcontainers
@InitAuthorsSqlScripts
@Import(TestcontainersConfiguration.class)
@DisplayName(value = "Интеграционные тесты сервиса по работе с авторами")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorServiceTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private AuthorService authorService;

    @Test
    @DisplayName("Удачное сохранение")
    void createSuccessTest() {
        AuthorDTO actual = authorService.create(
                new CreateAuthorDTO(
                        "Иванов",
                        null,
                        "Иван",
                        CountryUtils.COUNTRY_ID2,
                        null
                )
        );

        Assertions.assertEquals(8, authorRepository.count());
        Assertions.assertEquals("Иванов", actual.getLastName());
    }

    private static Stream<Arguments> updateSuccessTest() {
        return Stream.of(
                arguments(
                        named("Обновление Фамилии", AuthorUtils.AUTHOR_ID1),
                        new UpdateAuthorDTO(
                                "Иванов",
                                "Сергеевич",
                                null,
                                null,
                                LocalDate.of(1799, 6, 6)
                        ),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID1,
                                "Иванов",
                                "Сергеевич",
                                "Александр",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                LocalDate.of(1799, 6, 6),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление отчества", AuthorUtils.AUTHOR_ID3),
                        new UpdateAuthorDTO(
                                null,
                                "Иванович",
                                null,
                                null,
                                LocalDate.of(1860, 1, 29)
                        ),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID3,
                                "Чехов",
                                "Иванович",
                                "Антон",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                LocalDate.of(1860, 1, 29),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновления имени", AuthorUtils.AUTHOR_ID7),
                        new UpdateAuthorDTO(
                                null,
                                null,
                                "Набунага",
                                null,
                                LocalDate.of(1949, 1, 12)
                        ),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID7,
                                "Мураками",
                                null,
                                "Набунага",
                                new CountryDTO(CountryUtils.COUNTRY_ID3, null),
                                LocalDate.of(1949, 1, 12),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление страны", AuthorUtils.AUTHOR_ID6),
                        new UpdateAuthorDTO(
                                null,
                                null,
                                null,
                                CountryUtils.COUNTRY_ID3,
                                LocalDate.of(1955, 2, 17)
                        ),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID6,
                                "Мо",
                                null,
                                "Янь",
                                new CountryDTO(CountryUtils.COUNTRY_ID3, null),
                                LocalDate.of(1955, 2, 17),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление даты рождения", AuthorUtils.AUTHOR_ID7),
                        new UpdateAuthorDTO(
                                null,
                                null,
                                null,
                                null,
                                LocalDate.of(1950, 1, 1)
                        ),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID7,
                                "Мураками",
                                null,
                                "Харуки",
                                new CountryDTO(CountryUtils.COUNTRY_ID3, null),
                                LocalDate.of(1950, 1, 1),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Удаление даты рождения", AuthorUtils.AUTHOR_ID7),
                        new UpdateAuthorDTO(
                                null,
                                null,
                                null,
                                null,
                                null
                        ),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID7,
                                "Мураками",
                                null,
                                "Харуки",
                                new CountryDTO(CountryUtils.COUNTRY_ID3, null),
                                null,
                                null,
                                null
                        )
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное обновление")
    void updateSuccessTest(UUID id, UpdateAuthorDTO dto, AuthorDTO expected) {
        AuthorDTO actual = authorService.update(id, dto);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getLastName(), actual.getLastName());
        Assertions.assertEquals(expected.getMiddleName(), actual.getMiddleName());
        Assertions.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assertions.assertEquals(expected.getCountry().getId(), actual.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - автор по идентификатору не найден")
    void updateAuthorNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.update(authorId, new UpdateAuthorDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(AuthorByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - страна по идентификатору не найдена")
    void updateCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> authorService.update(
                        AuthorUtils.AUTHOR_ID4,
                        new UpdateAuthorDTO(
                                null,
                                null,
                                null,
                                countryId,
                                null
                        )
                )
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        authorService.delete(AuthorUtils.AUTHOR_ID4);

        Assertions.assertEquals(6, authorRepository.count());
    }

    @Test
    @DisplayName("Неудачое удаление - автор по идентификатору не найден")
    void deleteNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.delete(authorId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(AuthorByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        AuthorDTO actual = authorService.getById(AuthorUtils.AUTHOR_ID4);

        Assertions.assertEquals(AuthorUtils.AUTHOR_ID4, actual.getId());
    }

    @Test
    @DisplayName("Не удачное получение по идентификатору - Автор не найден")
    void getByIdNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.getById(authorId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(AuthorByIdNotFoundException.getErrorText(), actual.getMessage());
    }
}
