package ru.tdd.author.unit.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.tdd.author.application.dto.authors.AuthorDTO;
import ru.tdd.author.application.dto.authors.AuthorDetailsDTO;
import ru.tdd.author.application.dto.authors.CreateAuthorDTO;
import ru.tdd.author.application.dto.authors.UpdateAuthorDTO;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.exceptions.country.AuthorByIdNotFoundException;
import ru.tdd.author.application.mappers.AuthorMapper;
import ru.tdd.author.application.redis.CountryRedisService;
import ru.tdd.author.application.services.imp.AuthorServiceImp;
import ru.tdd.author.database.entitites.Author;
import ru.tdd.author.database.repositories.AuthorRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Набор модульных тестов сервиса по работе с авторами
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Модульное тестирование сервиса авторов")
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private CountryRedisService countryService;

    @InjectMocks
    private AuthorServiceImp authorService;

    @Test
    @DisplayName("Удачное сохранение")
    void saveSuccessTest() {
        UUID countryId = UUID.randomUUID();
        CountryDTO country = new CountryDTO(countryId, "Россия");

        Mockito.when(countryService.get(countryId)).thenReturn(country);
        Mockito.when(authorMapper.toDto(any(Author.class), any(CountryDTO.class))).thenReturn(
                new AuthorDTO(
                        UUID.randomUUID(),
                        "Попов",
                        null,
                        "Дмитрий",
                        country
                )
        );

        AuthorDTO actual = authorService.create(
                new CreateAuthorDTO(
                        "Попов",
                        null,
                        "Дмитрий",
                        countryId
                )
        );

        Assertions.assertEquals("Попов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
        Assertions.assertEquals("Дмитрий", actual.getFirstName());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        UUID countryId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        CountryDTO country = new CountryDTO(countryId, "Россия");
        Author author = new Author(
                "Иванов",
                "Иванович",
                "Иван",
                countryId
        );

        Mockito.when(countryService.get(countryId)).thenReturn(country);
        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        Mockito.when(authorMapper.toDto(any(Author.class), any(CountryDTO.class))).thenReturn(
                new AuthorDTO(
                        authorId,
                        "Попов",
                        null,
                        "Дмитрий",
                        country
                )
        );

        AuthorDTO actual = authorService.update(
                authorId,
                new UpdateAuthorDTO(
                        "Попов",
                        null,
                        "Дмитрий",
                        null
                )
        );

        Assertions.assertEquals(authorId, actual.getId());
        Assertions.assertEquals("Попов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
    }

    @Test
    @DisplayName("Не удачное обновление - автор по идентификатору не найден")
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
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        UUID authorId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Author author = new Author(
                "Иванов",
                null,
                "Иван",
                countryId
        );

        Mockito.when(countryService.get(countryId)).thenReturn(new CountryDTO(countryId, "Россия"));
        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        Mockito.when(authorMapper.toDetailsDto(any(Author.class), any(CountryDTO.class)))
                .thenReturn(
                        new AuthorDetailsDTO(
                                authorId,
                                "Иванов",
                                null,
                                "Иван",
                                null,
                                LocalDateTime.now(),
                                LocalDateTime.now()
                        )
                );

        AuthorDetailsDTO actual = authorService.getById(authorId);

        Assertions.assertEquals(authorId, actual.getId());
        Assertions.assertEquals("Иванов", actual.getLastName());
        Assertions.assertNull(actual.getMiddleName());
        Assertions.assertEquals("Иван", actual.getFirstName());
        Assertions.assertNull(actual.getCountry());
        Assertions.assertNotNull(actual.getCreationTime());
        Assertions.assertNotNull(actual.getUpdateTime());
    }

    @Test
    @DisplayName("Не удачное получение по идентификатору - автор по идентификатору не найден")
    void getByIdNotFoundTest() {
        UUID authorId = UUID.randomUUID();

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.getById(authorId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Автор с идентификатором: " + authorId + " не найден", actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        UUID authorId = UUID.randomUUID();

        Author author = new Author(
                "Иванов",
                "Иванович",
                "Иван",
                UUID.randomUUID()
        );

        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        authorService.delete(authorId);

        Mockito.verify(authorRepository).delete(any(Author.class));
    }

    @Test
    @DisplayName("Не удачное сохранение - автор по идентификатору не найден")
    void deleteNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        AuthorByIdNotFoundException actual = Assertions.assertThrows(
                AuthorByIdNotFoundException.class,
                () -> authorService.delete(authorId)
        );

        Assertions.assertEquals(
                HttpStatus.NOT_FOUND.value(),
                actual.getStatusCode()
        );
        Assertions.assertEquals(
                "Автор с идентификатором: " + authorId + " не найден",
                actual.getMessage()
        );
    }
}
