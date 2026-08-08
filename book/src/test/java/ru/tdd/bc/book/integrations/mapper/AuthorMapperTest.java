package ru.tdd.bc.book.integrations.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.book.TestcontainersConfiguration;
import ru.tdd.bc.book.application.dto.authors.AuthorDTO;
import ru.tdd.bc.book.application.dto.authors.AuthorDetailsDTO;
import ru.tdd.bc.book.application.mappers.AuthorMapper;
import ru.tdd.bc.book.database.entities.Author;
import ru.tdd.bc.book.database.entities.Country;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 23.02.2026
 * Набор тестов маппера авторов
 */
@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@DisplayName("Тестирование маппера авторов")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorMapperTest {

    private final AuthorMapper authorMapper;

    @Autowired
    AuthorMapperTest(
            AuthorMapper authorMapper
    ) {
        this.authorMapper = authorMapper;
    }

    @Test
    @DisplayName("Преобразование в базовый DTO")
    void mapAuthorTest() {
        Country country = new Country(UUID.randomUUID(), "Россия");

        AuthorDTO actual = authorMapper.toDto(
                new Author(
                        "Иванов",
                        null,
                        "Иван",
                        country
                )
        );

        Assertions.assertEquals("Иванов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
        Assertions.assertEquals("Иван", actual.getFirstName());
        Assertions.assertEquals(country.getId(), actual.getCountry().getId());
    }

    @Test
    @DisplayName("Преобразование в детальный DTO")
    void mapAuthorDetailsTest() {
        Country country = new Country(UUID.randomUUID(), "Россия");

        Author author =  new Author(
                "Иванов",
                null,
                "Иван",
                country
        );

        author.setUpdateTime(LocalDateTime.now());

        AuthorDetailsDTO actual = authorMapper.toDetailsDto(
                author
        );

        Assertions.assertEquals("Иванов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
        Assertions.assertEquals("Иван", actual.getFirstName());
        Assertions.assertEquals(country.getId(), actual.getCountry().getId());
        Assertions.assertEquals(LocalDate.now(), actual.getCreationTime().toLocalDate());
        Assertions.assertEquals(LocalDate.now(), actual.getUpdateTime().toLocalDate());
    }
}
