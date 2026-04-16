package ru.tdd.author.integrations.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.author.TestcontainersConfiguration;
import ru.tdd.author.application.dto.authors.AuthorDTO;
import ru.tdd.author.application.dto.authors.AuthorDetailsDTO;
import ru.tdd.author.application.dto.authors.CreateAuthorDTO;
import ru.tdd.author.application.dto.authors.UpdateAuthorDTO;
import ru.tdd.author.application.exceptions.country.AuthorByIdNotFoundException;
import ru.tdd.author.application.exceptions.country.CountryByIdNotFoundException;
import ru.tdd.author.application.services.AuthorService;
import ru.tdd.author.database.entitites.Author;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.AuthorRepository;
import ru.tdd.author.database.repositories.CountryRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Интегационные тесты сервиса по работе с авторами
 */
@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@DisplayName(value = "Интеграционные тесты сервиса по работе с авторами")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorServiceTest {

    private final AuthorRepository authorRepository;

    private final CountryRepository countryRepository;

    private final AuthorService authorService;

    @Autowired
    public AuthorServiceTest(
            AuthorRepository authorRepository,
            CountryRepository countryRepository,
            AuthorService authorService
    ) {
        this.authorRepository = authorRepository;
        this.countryRepository = countryRepository;
        this.authorService = authorService;
    }

    @BeforeEach
    void cleanDb() {
        authorRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    @DisplayName("Удачное сохранение")
    void createSuccessTest() {
        Country country = new Country("Россия");

        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        long expectedCount = authorRepository.count() + 1;

        AuthorDTO actual = authorService.create(
                new CreateAuthorDTO(
                        "Иванов",
                        null,
                        "Иван",
                        country.getId()
                )
        );

        long actualCount = authorRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
        Assertions.assertEquals("Иванов", actual.getLastName());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        Country country1 = new Country("Россия");
        Country country2 = new Country("Иран");

        country1.setId(UUID.randomUUID());
        country2.setId(UUID.randomUUID());

        countryRepository.saveAll(List.of(country1, country2));

        Author author = new Author(
                "Иванов",
                "Иванович",
                "Иван",
                country1.getId()
        );

        authorRepository.save(author);

        AuthorDTO actual = authorService.update(
                author.getId(),
                new UpdateAuthorDTO(
                        "Попов",
                        null,
                        "Поп",
                        country2.getId()
                )
        );

        Assertions.assertEquals("Попов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
        Assertions.assertEquals("Поп", actual.getFirstName());
        Assertions.assertEquals(country2.getId(), actual.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - автор по идентификатору не найден")
    void updateAuthorNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.update(authorId, new UpdateAuthorDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Автор с идентификатором: " + authorId + " не найден", actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - страна по идентификатору не найдена")
    void updateCountryNotFoundFailTest() {
        Country country = new Country("Россия");

        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        Author author = new Author(
                "Иванов",
                null,
                "Иван",
                country.getId()
        );

        authorRepository.save(author);

        UUID countryId = UUID.randomUUID();

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> authorService.update(
                        author.getId(),
                        new UpdateAuthorDTO(
                                null,
                                null,
                                null,
                                countryId
                        )
                )
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с идентификатором: " + countryId + " не найдена", actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        Country country = new Country("Россия");

        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        Author author1 = new Author(
                "Иванов",
                null,
                "Иван",
                country.getId()
        );

        Author author2 = new Author(
                "Иванов",
                null,
                "Иван",
                country.getId()
        );

        authorRepository.saveAll(List.of(author1, author2));

        authorService.delete(author2.getId());

        Assertions.assertEquals(1, authorRepository.count());
    }

    @Test
    @DisplayName("Неудачое удаление - автор по идентификатору не найден")
    void deleteNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.delete(authorId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Автор с идентификатором: " + authorId + " не найден", actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        Country country = new Country("Россия");

        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        Author author1 = new Author(
                "Иванов",
                null,
                "Иван",
                country.getId()
        );

        Author author2 = new Author(
                "Петров",
                null,
                "Петр",
                country.getId()
        );

        Author author3 = new Author(
                "Дмитриев",
                null,
                "Дмитрий",
                country.getId()
        );

        authorRepository.saveAll(List.of(author1, author2, author3));

        AuthorDetailsDTO actual = authorService.getById(author2.getId());

        Assertions.assertEquals(author2.getId(), actual.getId());
        Assertions.assertEquals(LocalDate.now(), actual.getCreationTime().toLocalDate());
        Assertions.assertEquals(LocalDate.now(), actual.getUpdateTime().toLocalDate());
    }

    @Test
    @DisplayName("Не удачное получение по идентификатору - Автор не найден")
    void getByIdNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.getById(authorId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Автор с идентификатором: " + authorId + " не найден", actual.getMessage());
    }
}
