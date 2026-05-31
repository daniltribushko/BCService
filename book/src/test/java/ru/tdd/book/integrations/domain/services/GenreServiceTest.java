package ru.tdd.book.integrations.domain.services;

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
import ru.tdd.book.TestcontainersConfiguration;
import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.GenreListDataDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.book.database.repositories.GenreRepository;
import ru.tdd.book.domain.exceptions.genre.GenreAlreadyExistsException;
import ru.tdd.book.domain.exceptions.genre.GenreByIdNotFoundException;
import ru.tdd.book.domain.services.genres.GenreService;
import ru.tdd.book.utils.GenreUtils;
import ru.tdd.book.utils.InitGenresSqlScripts;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static ru.tdd.book.utils.GenreUtils.GENRE_ID2;
import static ru.tdd.book.utils.GenreUtils.GENRE_ID4;

/**
 * @author Tribushko Danil
 * @since 07.06.2026
 */
@Testcontainers
@SpringBootTest
@InitGenresSqlScripts
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест сервиса по работе с жанрами")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreServiceTest {

    private final GenreService genreService;

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceTest(
            GenreService genreService,
            GenreRepository genreRepository
    ) {
        this.genreService = genreService;
        this.genreRepository = genreRepository;
    }

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        long expectedCount = genreRepository.count() + 1;

        GenreDto actual = genreService.create(new CreateGenreDto("Историческая литература"));

        long actualCount = genreRepository.count();

        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expectedCount, actualCount);
        Assertions.assertEquals("Историческая литература", actual.getName());
    }

    @Test
    @DisplayName("Неудачное создание - жанр уже создан")
    void saveAlreadyExistsFailTest() {
        GenreAlreadyExistsException actual = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.create(new CreateGenreDto("Детектив"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр: \"%s\" уже создан".formatted("Детектив"),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        GenreDto actual = genreService.update(GenreUtils.GENRE_ID3, new UpdateGenreDto("Эпос"));

        Assertions.assertEquals(GenreUtils.GENRE_ID3, actual.getId());
        Assertions.assertEquals("Эпос", actual.getName());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр не найден")
    void updateNotFoundFailTest() {
        UUID genreId = UUID.randomUUID();

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.update(genreId, new UpdateGenreDto("НоВыЙ жаНР"))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(genreId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() {
        GenreAlreadyExistsException actual = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.update(GENRE_ID2, new UpdateGenreDto("Повесть"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр: \"%s\" уже создан".formatted("Повесть"),
                actual.getMessage()
        );
    }

    static Stream<Arguments> getByIdSuccessTest() {
        return Stream.of(
                arguments(named("Идентификатор: %s".formatted(GENRE_ID2), GENRE_ID2)),
                arguments(named("Идентификатор: %s".formatted(GENRE_ID4), GENRE_ID4))
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest(UUID genreId) {
        GenreDto actual = genreService.getById(genreId);

        Assertions.assertEquals(genreId, actual.getId());
    }

    static Stream<Arguments> getByIdNotFoundFailTest() {
        UUID genreId1 = UUID.randomUUID();
        UUID genreId2 = UUID.randomUUID();

        return Stream.of(
                arguments(named("Идентификатор: %s".formatted(genreId1), genreId1)),
                arguments(named("Идентификтор: %s".formatted(genreId2), genreId2))
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Неудачное получение по идентификатору: жанр не найден")
    void getByIdNotFoundFailTest(UUID genreId) {
        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.getById(genreId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(genreId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        long expectedCount = genreRepository.count() - 1;

        genreService.delete(GenreUtils.GENRE_ID5);

        long actualCount = genreRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    @DisplayName("Неудачное удаление - жанр не найден")
    void deleteNotFoundFailTest() {
        UUID genreId = UUID.randomUUID();

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.delete(genreId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(genreId),
                actual.getMessage()
        );
    }

    static Stream<Arguments> findAllTest() {
        return Stream.of(
                arguments(named("Полнотекстовый поиск по названию 1", "иКа"), 0, 10, 3),
                arguments(named("Полнотекстовый поиск по названию 2", "А"), 0, 10, 3),
                arguments(named("Полнотекстовый поиск по названию 3", "пОвЕсТь"), 0, 10, 1),
                arguments(named("Полнотекстовый поиск с пустым названием", ""), 0, 10, 5),
                arguments(named("Полнотекстовый поиск без названия", null), 0, 10, 5),
                arguments(named("Полнотекстовый поиск с пагинацией 1", null), 1, 2, 2),
                arguments(named("Полнотекстовый поиск с пагинацией 2", null), 2, 2, 1)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка жанров")
    void findAllTest(String name, int page, int perPage, int expectedSize) {
        GenreListDataDto data = genreService.getAll(name, page, perPage);

        Assertions.assertEquals(expectedSize, data.getData().size());
    }
}
