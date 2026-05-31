package ru.tdd.book.integrations.controllers;

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
import ru.tdd.book.TestcontainersConfiguration;
import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.GenreListDataDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.book.utils.GenreUtils;
import ru.tdd.book.utils.InitGenresSqlScripts;
import ru.tdd.book.utils.UserUtils;
import ru.tdd.core.application.services.JwtService;
import ru.tdd.core.application.utils.UrlUtils;
import ru.tdd.core.controller.dto.ExceptionDTO;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;


/**
 * @author Tribushko Danil
 * @since 11.05.2026
 */
@Testcontainers
@InitGenresSqlScripts
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест контроллера жанров книг")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreControllerTest {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final String BASE_PATH = "/genre";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Успешное создание")
    void saveSuccessTest() {
        CreateGenreDto dto = new CreateGenreDto("Исторический роман");

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateGenreDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<GenreDto> actual = restTemplate.postForEntity(
                BASE_PATH,
                httpEntity,
                GenreDto.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное создание - жанр уже создан")
    void saveAlreadyExistsFailTest() {
        CreateGenreDto dto = new CreateGenreDto("Детектив");

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateGenreDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDTO> actual = restTemplate.postForEntity(
                BASE_PATH,
                httpEntity,
                ExceptionDTO.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(
                HttpStatus.CONFLICT.value(),
                actual.getBody().getStatusCode()
        );
        Assertions.assertEquals(
                "Жанр: \"%s\" уже создан".formatted("Детектив"),
                actual.getBody().getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное создание - пользователь не является администратором")
    void saveNotAdminFailTest() {
        CreateGenreDto dto = new CreateGenreDto("Новый жанр");

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateGenreDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.postForEntity(
                BASE_PATH,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    static Stream<Arguments> saveNotValidFailTest() {
        return Stream.of(
                Arguments.arguments(
                        named(
                                "Пустая строка в названии",
                                ""
                        )
                ),
                Arguments.arguments(
                        named(
                                "Отсуствие строки",
                                null
                        )
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Неудачное сохранение - данные не валидны")
    void saveNotValidFailTest(String name) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateGenreDto> httpEntity = new HttpEntity<>(
                new CreateGenreDto(name),
                headers
        );

        ResponseEntity<ExceptionDTO> actual = restTemplate.exchange(
                BASE_PATH,
                HttpMethod.POST,
                httpEntity,
                ExceptionDTO.class
        );

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actual.getStatusCode());
    }

    @Test
    @DisplayName("Удачное обновление жанра")
    void updateSuccessTest() {
        UpdateGenreDto dto = new UpdateGenreDto("Новый жанр");

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateGenreDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<GenreDto> actual = restTemplate.exchange(
                BASE_PATH + "/" + GenreUtils.GENRE_ID3,
                HttpMethod.PUT,
                httpEntity,
                GenreDto.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals("Новый жанр", actual.getBody().getName());

        HttpEntity<Object> httpEntity2 = new HttpEntity<>(headers);

        ResponseEntity<GenreDto> updatedEntity = restTemplate.exchange(
                BASE_PATH + "/" + GenreUtils.GENRE_ID3,
                HttpMethod.GET,
                httpEntity2,
                GenreDto.class
        );

        Assertions.assertEquals(HttpStatus.OK, updatedEntity.getStatusCode());
        Assertions.assertNotNull(updatedEntity.getBody());
        Assertions.assertEquals(GenreUtils.GENRE_ID3, updatedEntity.getBody().getId());
        Assertions.assertEquals("Новый жанр", updatedEntity.getBody().getName());
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() {
        UpdateGenreDto dto = new UpdateGenreDto("Повесть");

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateGenreDto> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDTO> actual = restTemplate.exchange(
                BASE_PATH + "/" + GenreUtils.GENRE_ID1,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDTO.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(
                "Жанр: \"%s\" уже создан".formatted("Повесть"),
                actual.getBody().getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное обновление - жанр не найден")
    void updateNotFoundFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateGenreDto> httpEntity = new HttpEntity<>(new UpdateGenreDto(), headers);

        UUID id = UUID.randomUUID();

        ResponseEntity<ExceptionDTO> actual = restTemplate.exchange(
                BASE_PATH + "/" + id,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDTO.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(id),
                actual.getBody().getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное обновление - пользователь не является администратором")
    void updateNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateGenreDto> httpEntity = new HttpEntity<>(new UpdateGenreDto(), headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_PATH + "/" + GenreUtils.GENRE_ID2,
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

        ResponseEntity<GenreDto> actual = restTemplate.exchange(
                BASE_PATH + "/" + GenreUtils.GENRE_ID5,
                HttpMethod.GET,
                httpEntity,
                GenreDto.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(GenreUtils.GENRE_ID5, actual.getBody().getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - жанр не найден")
    void getByIdNotFoundFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        var id = UUID.randomUUID();

        ResponseEntity<ExceptionDTO> actual = restTemplate.exchange(
                BASE_PATH + "/" + id,
                HttpMethod.GET,
                httpEntity,
                ExceptionDTO.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(id),
                actual.getBody().getMessage()
        );
    }

    @Test
    @DisplayName("Удачное удаление жанра")
    void deleteSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_PATH + "/" + GenreUtils.GENRE_ID4,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное удаление жанра - жанр не найден")
    void deleteNotFoundFailTest() {
        UUID id = UUID.randomUUID();
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDTO> actual = restTemplate.exchange(
                BASE_PATH + "/" + id,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDTO.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(
                "Жанр с идентификатором: \"%s\" не найден".formatted(id),
                actual.getBody().getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное удаление - пользователь не является администратором")
    void deleteNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDTO> actual = restTemplate.exchange(
                BASE_PATH + "/" + GenreUtils.GENRE_ID1,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDTO.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    static Stream<Arguments> findAllTest() {
        return Stream.of(
                Arguments.arguments(
                        named("Поиск с пустым названием 1", ""), 0, 10, 5
                ),
                Arguments.arguments(
                        named("Поиск с пустым названием 2", null), 0, 10, 5
                ),
                Arguments.arguments(
                        named("Полнотекстовый поиск 1", "ФаНтА"), 0, 10, 2
                ),
                Arguments.arguments(
                        named("Полнотекстовый поиск 2", "русская"), 0, 10, 1
                ),
                Arguments.arguments(
                        named("Пагинация 1", null), 1, 2, 2
                ),
                Arguments.arguments(
                        named("Пагинация 2", null), 0, 3, 3
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка жанров с фильтрами")
    void findAllTest(String name, int page, int perPage, int expectedSize) {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<GenreListDataDto> actual = restTemplate.exchange(
                UrlUtils.builder(BASE_PATH)
                        .add("name", name)
                        .add("page", page)
                        .add("per_page", perPage)
                        .build(),
                HttpMethod.GET,
                httpEntity,
                GenreListDataDto.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(
                expectedSize,
                actual.getBody().getData().size()
        );
    }
}
