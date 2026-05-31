package ru.tdd.book.integrations.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.book.TestcontainersConfiguration;
import ru.tdd.book.database.entities.Genre;
import ru.tdd.book.database.repositories.GenreRepository;
import ru.tdd.book.utils.InitGenresSqlScripts;

import java.util.Optional;
import java.util.UUID;

import static ru.tdd.book.utils.GenreUtils.GENRE2;
import static ru.tdd.book.utils.GenreUtils.GENRE_ID3;

/**
 * @author Tribushko Danil
 * @since 06.05.2026
 */
@DataJpaTest
@Testcontainers
@InitGenresSqlScripts
@DisplayName("Интеграционный тест репозитория жанров")
@Import(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreRepositoryTest {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreRepositoryTest(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Test
    @DisplayName("Успешное сохранение")
    void saveSuccessTest() {
        long allCount = genreRepository.count();

        genreRepository.save(new Genre("Тестовый жанр"));

        Assertions.assertEquals(allCount + 1, genreRepository.findAll().size());
    }

    @Test
    @DisplayName("Успешное обновление")
    void updateSuccessTest() {
        Genre genre = genreRepository.save(new Genre("Детектив"));

        genre.setName("Русская классика");

        Genre newGenre = genreRepository.save(genre);

        Assertions.assertEquals("Русская классика", newGenre.getName());
    }

    @Test
    @DisplayName("Успешное удаление")
    void deleteSuccessTest() {
        long allCount = genreRepository.count();

        genreRepository.delete(GENRE2);

        Assertions.assertEquals(allCount - 1, genreRepository.count());
    }

    @Test
    @DisplayName("Поиск по идентификатору")
    void getByIdSuccessTest() {
        Optional<Genre> genreOpt1 = genreRepository.findById(GENRE_ID3);
        Optional<Genre> genreOpt2 = genreRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(genreOpt1.isPresent());
        Assertions.assertTrue(genreOpt2.isEmpty());
    }
}
