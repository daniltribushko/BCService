package ru.tdd.author.integrations.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.author.TestcontainersConfiguration;
import ru.tdd.author.database.entitites.Author;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.AuthorRepository;
import ru.tdd.author.database.repositories.CountryRepository;
import ru.tdd.author.database.specifications.AuthorSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * Набор тестов репозитория для работы с авторами
 */
@DataJpaTest
@Testcontainers
@Import(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Тестирование репозитория авторов")
class AuthorRepositoryTest {

    private final AuthorRepository authorRepository;

    private final CountryRepository countryRepository;

    @Autowired
    AuthorRepositoryTest(
            AuthorRepository authorRepository,
            CountryRepository countryRepository
    ) {
        this.authorRepository = authorRepository;
        this.countryRepository = countryRepository;
    }

    @BeforeEach
    void cleanDb() {
        authorRepository.deleteAll();
    }

    @Test
    @DisplayName("Поиск только по фамилии автора")
    void findAllByLastNameTest() {
        Author author1 = new Author("Иванов", "", "", UUID.randomUUID());
        Author author2 = new Author("Иван", "", "", UUID.randomUUID());
        Author author3 = new Author("Попов", "", "", UUID.randomUUID());

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(
                2,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "вАн",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
        Assertions.assertEquals(
                3,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "В",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск только по отчеству автора")
    void findAllByMiddleNameTest() {
        Author author1 = new Author("", "Дмитриевич", "", UUID.randomUUID());
        Author author2 = new Author("", "", "", UUID.randomUUID());
        Author author3 = new Author("", "Иванович", "", UUID.randomUUID());

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(
                2,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "вИч",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
        Assertions.assertEquals(
                1,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "ДмИтР",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск только по имени автора")
    void findAllByFirstNameTest() {
        Author author1 = new Author("", null, "Данил", UUID.randomUUID());
        Author author2 = new Author("", null, "Даниил", UUID.randomUUID());
        Author author3 = new Author("", null, "Иван", UUID.randomUUID());

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(
                2,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "ДАни",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
        Assertions.assertEquals(
                1,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "ВАН",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск по фамилии, отчеству, имени автору")
    void findAllByFullFioTest() {
        Author author1 = new Author("Иванов", "Николаевич", "Данил", UUID.randomUUID());
        Author author2 = new Author("Пушкин", "Иванович", "Антон", UUID.randomUUID());
        Author author3 = new Author("Попов", null, "Иван", UUID.randomUUID());

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(
                3,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "Иван",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск с пустым текстом")
    void findAllByEmptyFioTest() {
        Author author1 = new Author("Иванов", "Николаевич", "Данил", UUID.randomUUID());
        Author author2 = new Author("Пушкин", "Иванович", "Антон", UUID.randomUUID());
        Author author3 = new Author("Попов", null, "Иван", UUID.randomUUID());

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(
                3,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "",
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
        Assertions.assertEquals(
                3,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск с минимальным временем создания")
    void findAllByStartCreationTime() {
        Author author1 = new Author("", null, "", UUID.randomUUID());
        author1.setCreationTime(LocalDateTime.of(1999, 1, 1, 1, 1, 1));
        Author author2 = new Author("", null, "", UUID.randomUUID());
        author2.setCreationTime(LocalDateTime.of(2005, 1, 1, 1, 1, 1));
        Author author3 = new Author("", null, "", UUID.randomUUID());
        author3.setCreationTime(LocalDateTime.of(2014, 1, 1, 1, 1, 1));

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(2, authorRepository.findAll(AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                null,
                        null,
                                LocalDateTime.of(2005, 1, 1, 1, 1, 1),
                                null,
                                null,
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск с максимальным временем создания")
    void findAllByEndCreationTime() {
        Author author1 = new Author("", null, "", UUID.randomUUID());
        author1.setCreationTime(LocalDateTime.of(1999, 1, 1, 1, 1, 1));
        Author author2 = new Author("", null, "", UUID.randomUUID());
        author2.setCreationTime(LocalDateTime.of(2005, 1, 1, 1, 1, 1));
        Author author3 = new Author("", null, "", UUID.randomUUID());
        author3.setCreationTime(LocalDateTime.of(2014, 1, 1, 1, 1, 1));

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(2, authorRepository.findAll(AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                null,
                        null,
                                null,
                                LocalDateTime.of(2005, 1, 1, 1, 1, 1),
                                null,
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск с минимальным временем создания")
    void findAllByStartUpdateTime() {
        Author author1 = new Author("", null, "", UUID.randomUUID());
        author1.setUpdateTime(LocalDateTime.of(1999, 1, 1, 1, 1, 1));
        Author author2 = new Author("", null, "", UUID.randomUUID());
        author2.setUpdateTime(LocalDateTime.of(2005, 1, 1, 1, 1, 1));
        Author author3 = new Author("", null, "", UUID.randomUUID());
        author3.setUpdateTime(LocalDateTime.of(2014, 1, 1, 1, 1, 1));

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(2, authorRepository.findAll(AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                null,
                        null,
                                null,
                                null,
                                LocalDateTime.of(2005, 1, 1, 1, 1, 1),
                                null
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск с максимальным временем создания")
    void findAllByEndUpdateTime() {
        Author author1 = new Author("", null, "", UUID.randomUUID());
        author1.setUpdateTime(LocalDateTime.of(1999, 1, 1, 1, 1, 1));
        Author author2 = new Author("", null, "", UUID.randomUUID());
        author2.setUpdateTime(LocalDateTime.of(2005, 1, 1, 1, 1, 1));
        Author author3 = new Author("", null, "", UUID.randomUUID());
        author3.setUpdateTime(LocalDateTime.of(2014, 1, 1, 1, 1, 1));

        authorRepository.saveAll(List.of(author1, author2, author3));

        Assertions.assertEquals(2, authorRepository.findAll(AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                null,
                        null,
                                null,
                                null,
                                null,
                                LocalDateTime.of(2005, 1, 1, 1, 1, 1)
                        )
                ).size()
        );
    }

    @Test
    @DisplayName("Поиск по фамилии, отчеству, имени автору, времени создания, времени обновления")
    void findAllByFullFioAndDataTest() {
        Country country = new Country("Россия");

        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        Author author1 = new Author("Иванов", "Николаевич", "Данил", UUID.randomUUID());
        author1.setCreationTime(LocalDateTime.of(1999, 1, 1, 1, 1, 1));
        author1.setUpdateTime(LocalDateTime.of(2025, 1, 1, 1, 1, 1));

        Author author2 = new Author("Пушкин", "Иванович", "Антон", UUID.randomUUID());
        author1.setCreationTime(LocalDateTime.of(2004, 1, 1, 1, 1, 1));
        author1.setUpdateTime(LocalDateTime.of(2009, 3, 2, 5, 1, 1));

        Author author3 = new Author("Попов", null, "Иван", UUID.randomUUID());
        author1.setCreationTime(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        author1.setUpdateTime(LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        Author author4 = new Author("Антонов", "Антон", "Антонович", UUID.randomUUID());
        author1.setCreationTime(LocalDateTime.of(2017, 1, 1, 1, 1, 1));
        author1.setUpdateTime(LocalDateTime.of(2017, 3, 1, 1, 1, 1));

        Author author5 = new Author("Жуков", "Георгий", "Константинович", UUID.randomUUID());
        author1.setCreationTime(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        author1.setUpdateTime(LocalDateTime.of(2003, 1, 1, 1, 1, 1));

        Author author6 = new Author("Иванов", null, "Иван", country.getId());
        author6.setCreationTime(LocalDateTime.of(2011, 1, 1, 1, 1, 1));
        author1.setUpdateTime(LocalDateTime.of(2027, 1, 1, 1, 1, 1));

        authorRepository.saveAll(List.of(author1, author2, author3, author4, author5, author6));

        Assertions.assertEquals(
                1,
                authorRepository.findAll(
                        AuthorSpecification.byFioAndCountryNameAndVersionsDate(
                                "Иван",
                                "Росс",
                                LocalDateTime.of(2008, 1, 1, 1, 1, 1),
                                LocalDateTime.of(2017, 1, 1, 1, 1, 1),
                                LocalDateTime.of(2005, 1, 1, 1, 1, 1),
                                LocalDateTime.of(2028, 1, 1, 1, 1, 1)
                        )
                ).size()
        );
    }
}
