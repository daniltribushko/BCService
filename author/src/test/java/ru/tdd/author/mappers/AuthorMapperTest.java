package ru.tdd.author.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tdd.author.application.dto.authors.AuthorDTO;
import ru.tdd.author.application.dto.authors.AuthorDetailsDTO;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.mappers.AuthorMapper;
import ru.tdd.author.database.entitites.Author;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 23.02.2026
 * Набор тестов маппера авторов
 */
@SpringBootTest
@DisplayName("Тестирование маппера авторов")
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
        CountryDTO country = new CountryDTO(UUID.randomUUID(), "Россия");

        AuthorDTO actual = authorMapper.toDto(
                new Author(
                        "Иванов",
                        null,
                        "Иван",
                        country.getId()
                ),
                country
        );

        Assertions.assertEquals("Иванов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
        Assertions.assertEquals("Иван", actual.getFirstName());
        Assertions.assertEquals(country.getId(), actual.getCountry().getId());
    }

    @Test
    @DisplayName("Преобразование в детальный DTO")
    void mapAuthorDetailsTest() {
        CountryDTO country = new CountryDTO(UUID.randomUUID(), "Россия");

        Author author =  new Author(
                "Иванов",
                null,
                "Иван",
                country.getId()
        );

        author.setCreationTime(LocalDateTime.now());
        author.setUpdateTime(LocalDateTime.now());

        AuthorDetailsDTO actual = authorMapper.toDetailsDto(
                author,
                country
        );

        Assertions.assertEquals("Иванов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
        Assertions.assertEquals("Иван", actual.getFirstName());
        Assertions.assertEquals(country.getId(), actual.getCountry().getId());
        Assertions.assertEquals(LocalDate.now(), actual.getCreationTime().toLocalDate());
        Assertions.assertEquals(LocalDate.now(), actual.getUpdateTime().toLocalDate());
    }
}
