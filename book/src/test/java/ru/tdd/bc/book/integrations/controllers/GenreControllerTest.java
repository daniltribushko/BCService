package ru.tdd.bc.book.integrations.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.book.TestcontainersConfiguration;
import ru.tdd.bc.book.application.exceptions.GenreAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.GenreByIdNotFoundException;
import ru.tdd.bc.book.database.repositories.GenreRepository;
import ru.tdd.bc.book.sql.InitGenresSqlScripts;
import ru.tdd.bc.book.utils.GenreUtils;
import ru.tdd.bc.book.utils.UserUtils;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionariesListDataDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.UrlUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 14.08.2026
 */
@Testcontainers
@InitGenresSqlScripts
@Import(TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест контроллера жанров")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreControllerTest {

    private static final String BASE_URL = "/books/genres";

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateDictionaryDto dto = new CreateDictionaryDto("Автобиография");

        HttpEntity<CreateDictionaryDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<DictionaryDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                DictionaryDto.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        DictionaryDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getId());
        Assertions.assertEquals("Автобиография", body.getName());
    }

    @Test
    @DisplayName("Неудачное создание - жанр уже создан")
    void saveAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateDictionaryDto dto = new CreateDictionaryDto("Триллер");

        HttpEntity<CreateDictionaryDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(GenreAlreadyExistsException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное создание - данные не валидны")
    void saveNotValidFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateDictionaryDto> httpEntity = new HttpEntity<>(new CreateDictionaryDto(), headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
    }

    @Test
    @DisplayName("Неудачное создание - пользователь не является администратором")
    void saveNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateDictionaryDto dto = new CreateDictionaryDto("Комикс");

        HttpEntity<CreateDictionaryDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdateDictionaryDto dto = new UpdateDictionaryDto("Автобиография");

        HttpEntity<UpdateDictionaryDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<DictionaryDto> actual = restTemplate.exchange(
                BASE_URL + "/" + GenreUtils.GENRE_ID3,
                HttpMethod.PUT,
                httpEntity,
                DictionaryDto.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        DictionaryDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(GenreUtils.GENRE_ID3, body.getId());
        Assertions.assertEquals("Автобиография", body.getName());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр не найден")
    void updateNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateDictionaryDto> httpEntity = new HttpEntity<>(new UpdateDictionaryDto(), headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdateDictionaryDto dto = new UpdateDictionaryDto("Стимпанк");

        HttpEntity<UpdateDictionaryDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + GenreUtils.GENRE_ID2,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(GenreAlreadyExistsException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - пользователь не является администратором")
    void updateNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateDictionaryDto> httpEntity = new HttpEntity<>(new UpdateDictionaryDto(), headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + GenreUtils.GENRE_ID5,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<DictionaryDto> actual = restTemplate.exchange(
                BASE_URL + "/" + GenreUtils.GENRE_ID6,
                HttpMethod.GET,
                httpEntity,
                DictionaryDto.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        DictionaryDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(GenreUtils.GENRE_ID6, body.getId());
        Assertions.assertEquals("Любовный роман", body.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - жанр не найден")
    void getByIdNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.GET,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Успешное удаление")
    void deleteSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + GenreUtils.GENRE_ID7,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        Assertions.assertEquals(7, genreRepository.count());
    }

    @Test
    @DisplayName("Неудачное удаление - жанр не найден")
    void deleteNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(GenreByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное удаление - пользователь не является администратором")
    void deleteNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + GenreUtils.GENRE_ID8,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    private static Stream<Arguments> findAllTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "РоМаН"), null, null, 2),
                arguments(named("Поиск по названию 2", "АНК"), null, null, 1),
                arguments(named("Поиск по названию 3", "автобиография"), null, null, 0),
                arguments(named("Пагинация 1", null), 2, 3, 2),
                arguments(named("Пагинация 2", null), 1, 5, 3)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение жанров  фильтрами")
    void findAllTest(String name, Integer page, Integer perPage, long expectedSize) {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        var urlBuilder = UrlUtils.builder(BASE_URL)
                .add("name", name);

        if (page != null)
            urlBuilder.add("page", page);
        if (perPage != null)
            urlBuilder.add("per_page", perPage);

        ResponseEntity<DictionariesListDataDto> actual = restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                DictionariesListDataDto.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        DictionariesListDataDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedSize, body.getData().size());
    }
}
