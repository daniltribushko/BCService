package ru.tdd.book.modules.domain.services;

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
import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.book.database.entities.Genre;
import ru.tdd.book.database.repositories.GenreRepository;
import ru.tdd.book.database.services.GenreDbService;
import ru.tdd.book.domain.exceptions.genre.GenreAlreadyExistsException;
import ru.tdd.book.domain.exceptions.genre.GenreByIdNotFoundException;
import ru.tdd.book.domain.mappers.GenreMapper;
import ru.tdd.book.domain.services.genres.GenreServiceImp;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 06.05.2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Модульный тест сервиса жанров")
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @Mock
    private GenreDbService genreDbService;

    @InjectMocks
    private GenreServiceImp genreService;

    @Test
    @DisplayName("Успешное сохранение")
    void saveSuccessTest() {
        UUID genreId = UUID.randomUUID();
        Genre genre = new Genre("Научная фантастика");
        genre.setId(genreId);

        Mockito.when(genreRepository.exists(any(Specification.class)))
                .thenReturn(false);

        Mockito.when(genreRepository.save(any(Genre.class)))
                .thenReturn(genre);

        Mockito.when(genreMapper.toDto(genre))
                .thenReturn(
                        new GenreDto(genreId, "Научная фантастика")
                );

        GenreDto actual = genreService.create(
                new CreateGenreDto("Научная фантастика")
        );

        Mockito.verify(genreMapper, Mockito.times(1))
                .toDto(genre);
        Mockito.verify(genreRepository, Mockito.times(1))
                .save(any(Genre.class));
        Mockito.verify(genreRepository, Mockito.times(1))
                .exists(any(Specification.class));

        Assertions.assertEquals(genreId, actual.getId());
        Assertions.assertEquals("Научная фантастика", genre.getName());
    }

    @Test
    @DisplayName("Неудачное сохранение - жанр уже создан")
    void saveAlreadyExistsFailTest() {
        Mockito.when(genreRepository.exists(any(Specification.class)))
                .thenReturn(true);

        GenreAlreadyExistsException exception = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.create(new CreateGenreDto("Новый жанр"))
        );

        Mockito.verify(genreRepository).exists(any(Specification.class));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        Assertions.assertEquals(
                "Жанр: \"%s\" уже создан".formatted("Новый жанр"),
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(genreId)
                .name("Научная фантастика")
                .build();

        UpdateGenreDto updateDto = new UpdateGenreDto("Детектив");

        Mockito.when(genreDbService.getById(genreId)).thenReturn(genre);
        Mockito.when(genreRepository.exists(any(Specification.class))).thenReturn(false);

        Mockito.doAnswer(invocation -> {
                    UpdateGenreDto dto = invocation.getArgument(0);
                    Genre entity = invocation.getArgument(1);

                    entity.setName(dto.getName());

                    return null;
                }
        ).when(genreMapper).update(updateDto, genre);

        Mockito.doAnswer(invocation -> {
            Genre entity = invocation.getArgument(0);

            return new GenreDto(entity.getId(), entity.getName());
        }).when(genreMapper).toDto(any(Genre.class));

        GenreDto actual = genreService.update(genreId, updateDto);

        Mockito.verify(genreMapper).toDto(genre);
        Mockito.verify(genreRepository).save(genre);
        Mockito.verify(genreDbService).getById(genreId);
        Mockito.verify(genreRepository).exists(any(Specification.class));

        Assertions.assertEquals(genreId, actual.getId());
        Assertions.assertEquals("Детектив", actual.getName());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр не найден")
    void updateNotFoundFailTest() {
        UUID genreId = UUID.randomUUID();

        Mockito.when(genreDbService.getById(genreId))
                .thenThrow(new GenreByIdNotFoundException(genreId));

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.update(genreId, new UpdateGenreDto())
        );

        Mockito.verify(genreDbService).getById(genreId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(genreId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() {
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(genreId)
                .name("Фэнтези")
                .build();

        Mockito.when(genreDbService.getById(genreId))
                .thenReturn(genre);
        Mockito.when(genreRepository.exists(any(Specification.class)))
                .thenReturn(true);

        GenreAlreadyExistsException actual = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.update(genreId, new UpdateGenreDto("Новый жанр"))
        );

        Mockito.verify(genreDbService).getById(genreId);
        Mockito.verify(genreRepository).exists(any(Specification.class));

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр: \"%s\" уже создан".formatted("Новый жанр"),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачный поиск по идентификатору")
    void getByIdSuccessTest() {
        UUID genreId = UUID.randomUUID();

        Genre genre = Genre.builder()
                .id(genreId)
                .name("Фэнтези")
                .build();

        GenreDto genreDto = new GenreDto(
                genreId,
                "Фэнтези"
        );

        Mockito.when(genreDbService.getById(genreId)).thenReturn(genre);
        Mockito.when(genreMapper.toDto(genre)).thenReturn(genreDto);

        GenreDto actual = genreService.getById(genreId);

        Mockito.verify(genreMapper).toDto(genre);
        Mockito.verify(genreDbService).getById(genreId);

        Assertions.assertEquals(genreId, actual.getId());
        Assertions.assertEquals("Фэнтези", actual.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатоу - жанр не найден")
    void getByIdNotFoundFailTest() {
        UUID genreId = UUID.randomUUID();

        Mockito.when(genreDbService.getById(genreId)).thenThrow(
                new GenreByIdNotFoundException(genreId)
        );

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.getById(genreId)
        );

        Mockito.verify(genreDbService).getById(genreId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(genreId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(genreId)
                .name("Фэнтези")
                .build();

        Mockito.when(genreDbService.getById(genreId)).thenReturn(genre);

        genreService.delete(genreId);

        Mockito.verify(genreRepository).delete(genre);
        Mockito.verify(genreDbService).getById(genreId);
    }

    @Test
    @DisplayName("Неудачное удаление - жанр не найден")
    void deleteNotFoundFailTest() {
        UUID genreId = UUID.randomUUID();

        Mockito.when(genreDbService.getById(genreId)).thenThrow(new GenreByIdNotFoundException(genreId));

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.delete(genreId)
        );

        Mockito.verify(genreDbService).getById(genreId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(genreId),
                actual.getMessage()
        );
    }
}
