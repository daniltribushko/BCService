package ru.tdd.bc.book.unit.services;

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
import ru.tdd.bc.book.application.exceptions.GenreAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.GenreByIdNotFoundException;
import ru.tdd.bc.book.application.mappers.GenreMapper;
import ru.tdd.bc.book.application.services.imp.GenreServiceImp;
import ru.tdd.bc.book.database.entities.Genre;
import ru.tdd.bc.book.database.repositories.GenreRepository;
import ru.tdd.bc.book.database.service.GenreDbService;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 11.08.2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Модульное тестирование сервиса жанров")
public class GenreServiceTest {

    @Mock
    private GenreMapper genreMapper;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreDbService genreDbService;

    @InjectMocks
    private GenreServiceImp genreService;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        UUID dictionaryId = UUID.randomUUID();

        Mockito.when(genreRepository.exists(any(Specification.class)))
                .thenReturn(false);
        Mockito.when(genreMapper.toEntity(any(CreateDictionaryDto.class)))
                .thenReturn(new Genre("Автобиография"));
        Mockito.when(genreMapper.toDto(any(Genre.class)))
                .thenReturn(new DictionaryDto(dictionaryId, "Автобиография"));

        DictionaryDto dto = genreService.create(new CreateDictionaryDto("Автобиография"));

        Mockito.verify(genreRepository, Mockito.times(1)).exists(any(Specification.class));
        Mockito.verify(genreRepository, Mockito.times(1)).save(any(Genre.class));
        Mockito.verify(genreMapper, Mockito.times(1)).toEntity(any(CreateDictionaryDto.class));
        Mockito.verify(genreMapper, Mockito.times(1)).toDto(any(Genre.class));

        Assertions.assertEquals(dictionaryId, dto.getId());
        Assertions.assertEquals("Автобиография", dto.getName());
    }

    @Test
    @DisplayName("Неудачное создание - жанр уже создан")
    void saveAlreadyExistsFailTest() {
        Mockito.when(genreRepository.exists(any(Specification.class)))
                .thenReturn(true);
        Mockito.when(genreMapper.toEntity(any(CreateDictionaryDto.class)))
                .thenReturn(new Genre("Автобиография"));

        GenreAlreadyExistsException actual = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.create(new CreateDictionaryDto("Автобиография"))
        );

        Mockito.verify(genreRepository, Mockito.times(1)).exists(any(Specification.class));
        Mockito.verify(genreRepository, Mockito.times(0)).save(any(Genre.class));
        Mockito.verify(genreMapper, Mockito.times(1)).toEntity(any(CreateDictionaryDto.class));
        Mockito.verify(genreMapper, Mockito.times(0)).toDto(any(Genre.class));

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(GenreAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        UUID id = UUID.randomUUID();
        Genre genre = new Genre(id, "Комикс");

        Mockito.when(genreDbService.getById(id))
                .thenReturn(genre);
        Mockito.when(genreRepository.exists(any(Specification.class)))
                .thenReturn(false);
        Mockito.when(genreMapper.toDto(any(Genre.class)))
                .thenReturn(new DictionaryDto(id, "Автобиография"));

        DictionaryDto dto = genreService.update(id, new UpdateDictionaryDto("Автобиография"));

        Assertions.assertEquals(id, dto.getId());
        Assertions.assertEquals("Автобиография", dto.getName());
    }

    @Test
    @DisplayName("Неудачное обновление - Жанр не найден")
    void updateNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(genreDbService.getById(id))
                .thenThrow(new GenreByIdNotFoundException());

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.update(id, new UpdateDictionaryDto("Автобиография"))
        );

        Mockito.verify(genreRepository, Mockito.times(0)).exists(any(Specification.class));
        Mockito.verify(genreRepository, Mockito.times(0)).save(any(Genre.class));
        Mockito.verify(genreMapper, Mockito.times(0)).toDto(any(Genre.class));
        Mockito.verify(genreDbService, Mockito.times(1)).getById(id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(genreDbService.getById(id)).thenReturn(new Genre(id, "Фэнтези"));
        Mockito.when(genreRepository.exists(any(Specification.class))).thenReturn(true);

        GenreAlreadyExistsException actual = Assertions.assertThrows(
                GenreAlreadyExistsException.class,
                () -> genreService.update(id, new UpdateDictionaryDto("Автобиография"))
        );

        Mockito.verify(genreRepository, Mockito.times(1)).exists(any(Specification.class));
        Mockito.verify(genreRepository, Mockito.times(0)).save(any(Genre.class));
        Mockito.verify(genreMapper, Mockito.times(0)).toDto(any(Genre.class));
        Mockito.verify(genreDbService, Mockito.times(1)).getById(id);

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(GenreAlreadyExistsException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(genreDbService.getById(id))
                .thenReturn(new Genre(id, "Научно-популярная литература"));
        Mockito.when(genreMapper.toDto(any(Genre.class)))
                .thenReturn(new DictionaryDto(id, "Научно-популярная литература"));

        DictionaryDto dto = genreService.getById(id);

        Mockito.verify(genreDbService, Mockito.times(1)).getById(id);
        Mockito.verify(genreMapper, Mockito.times(1)).toDto(any(Genre.class));

        Assertions.assertEquals(id, dto.getId());
        Assertions.assertEquals("Научно-популярная литература", dto.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатоу - жанр не найден")
    void getByIdNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(genreDbService.getById(id)).thenThrow(new GenreByIdNotFoundException());

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.getById(id)
        );

        Mockito.verify(genreDbService, Mockito.times(1)).getById(id);
        Mockito.verify(genreMapper, Mockito.times(0)).toDto(any(Genre.class));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(genreDbService.getById(id)).thenReturn(new Genre(id, "Любовный роман"));

        genreService.delete(id);

        Mockito.verify(genreDbService, Mockito.times(1)).getById(id);
        Mockito.verify(genreRepository, Mockito.times(1)).delete(any(Genre.class));
    }

    @Test
    @DisplayName("Неудачное удаление - жанр не найден")
    void deleteNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(genreDbService.getById(id)).thenThrow(new GenreByIdNotFoundException());

        GenreByIdNotFoundException actual = Assertions.assertThrows(
                GenreByIdNotFoundException.class,
                () -> genreService.delete(id)
        );

        Mockito.verify(genreDbService, Mockito.times(1)).getById(id);
        Mockito.verify(genreRepository, Mockito.times(0)).delete(any(Genre.class));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), actual.getMessage());
    }
}
