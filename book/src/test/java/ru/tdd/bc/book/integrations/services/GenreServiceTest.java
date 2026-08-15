package ru.tdd.bc.book.integrations.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.book.TestcontainersConfiguration;
import ru.tdd.bc.book.application.exceptions.GenreAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.GenreByIdNotFoundException;
import ru.tdd.bc.book.application.services.GenreService;
import ru.tdd.bc.book.database.repositories.GenreRepository;
import ru.tdd.bc.book.sql.InitGenresSqlScripts;
import ru.tdd.bc.book.utils.GenreUtils;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 09.08.2026
 */
@Testcontainers
@SpringBootTest
@InitGenresSqlScripts
@Import(TestcontainersConfiguration.class)
@DisplayName("Интеграционные тесты сервиса для работы с жанрами")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreServiceTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        DictionaryDto actual = genreService.create(new CreateDictionaryDto("Автобиография"));

        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals("Автобиография", actual.getName());
        Assertions.assertEquals(9, genreRepository.count());
    }

    @Test
    @DisplayName("Неудачное создание - жанр уже создан")
    void saveAlreadyExistsFailTest() {
        GenreAlreadyExistsException actual = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.create(new CreateDictionaryDto("Комикс"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(GenreAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        DictionaryDto actual = genreService.update(
                GenreUtils.GENRE_ID1,
                new UpdateDictionaryDto("Автобиография")
        );

        Assertions.assertEquals(GenreUtils.GENRE_ID1, actual.getId());
        Assertions.assertEquals("Автобиография", actual.getName());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр не найден")
    void updateNotFoundFailTest() {
        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.update(UUID.randomUUID(), new UpdateDictionaryDto("Автобиография"))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() {
        GenreAlreadyExistsException actual = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.update(GenreUtils.GENRE_ID3,  new UpdateDictionaryDto("Стимпанк"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(GenreAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        DictionaryDto actual = genreService.getById(GenreUtils.GENRE_ID6);

        Assertions.assertEquals(GenreUtils.GENRE_ID6, actual.getId());
        Assertions.assertEquals("Любовный роман", actual.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - Жанр не найден")
    void getByIdNotFoundFailTest() {
        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.getById(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        genreService.delete(GenreUtils.GENRE_ID3);

        Assertions.assertEquals(7, genreRepository.count());
    }

    @Test
    @DisplayName("Неудачное удаление - жанр не найден")
    void deleteNotFoundFailTest() {
        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.delete(UUID.randomUUID())
        );

        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), actual.getMessage());
    }
}
