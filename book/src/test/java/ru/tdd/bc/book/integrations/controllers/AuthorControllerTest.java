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
import ru.tdd.bc.book.application.dto.authors.AuthorDTO;
import ru.tdd.bc.book.application.dto.authors.AuthorListDTO;
import ru.tdd.bc.book.application.dto.authors.CreateAuthorDTO;
import ru.tdd.bc.book.application.dto.authors.UpdateAuthorDTO;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.exceptions.AuthorByIdNotFoundException;
import ru.tdd.bc.book.database.repositories.AuthorRepository;
import ru.tdd.bc.book.sql.InitAuthorsSqlScripts;
import ru.tdd.bc.book.utils.AuthorUtils;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.book.utils.UserUtils;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.UrlUtils;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * Набор тестов для контроллера автоа
 */
@Testcontainers
@InitAuthorsSqlScripts
@DisplayName("Интеграционные тесты контроллера авторов")
@Import(value = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorControllerTest {

    private static final String BASE_URL = "/books/authors";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthorRepository authorRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    @DisplayName("Удачное сохранение")
    void successTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateAuthorDTO dto = new CreateAuthorDTO(
                "Пикуль",
                "Саввич",
                "Валентин",
                CountryUtils.COUNTRY_ID1,
                LocalDate.of(1928, 7, 13)
        );

        HttpEntity<CreateAuthorDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<AuthorDTO> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                AuthorDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        AuthorDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getId());
        Assertions.assertEquals("Пикуль", body.getLastName());
        Assertions.assertEquals("Саввич", body.getMiddleName());
        Assertions.assertEquals("Валентин", body.getFirstName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, body.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное сохранение - страна по идентификатору не найдена")
    void saveCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateAuthorDTO dto = new CreateAuthorDTO(
                "Иван",
                "Иванович",
                "Иван",
                countryId,
                LocalDate.of(1999, 1, 1)
        );

        HttpEntity<CreateAuthorDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), body.getMessage());
    }

    private static Stream<Arguments> saveCountryDtoNotValidFailTest() {
        return Stream.of(
                arguments(
                        named(
                                "Не заполнена фамилия",
                                new CreateAuthorDTO(null, null, "Иван", UUID.randomUUID(), LocalDate.of(1999, 1, 1))
                        ),
                        named(
                                "Не заполнено имя",
                                new CreateAuthorDTO("Иванов", null, "", UUID.randomUUID(), null)
                        ),
                        named(
                                "Не заполнен идентификатор страны",
                                new CreateAuthorDTO("Иванов", null, "Иван", null, null)
                        ),
                        named(
                                "Невалидная дата",
                                new CreateAuthorDTO("Иванов", null, "Иван", CountryUtils.COUNTRY_ID1, LocalDate.of(2999, 1, 1))
                        )
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Неудачное сохранение - дто не валидный")
    void saveCountryDtoNotValidFailTest(CreateAuthorDTO dto) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateAuthorDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное сохранение - пользователь не является администатором")
    void saveUserNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        CreateAuthorDTO dto = new CreateAuthorDTO("Иванов", null, "Иван", UUID.randomUUID(), null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateAuthorDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    private static Stream<Arguments> updateSuccessTest() {
        return Stream.of(
                arguments(
                        named("Обновление фамилии", AuthorUtils.AUTHOR_ID1),
                        new UpdateAuthorDTO("Попов", "Сергеевич", null, null, null),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID1,
                                "Попов",
                                "Сергеевич",
                                "Александр",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                LocalDate.of(1799, 6, 6),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление отчества", AuthorUtils.AUTHOR_ID2),
                        new UpdateAuthorDTO(null, "Дмитриевич", null, null, null),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID2,
                                "Достоевский",
                                "Дмитриевич",
                                "Фёдор",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                LocalDate.of(1821, 11, 11),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление имени", AuthorUtils.AUTHOR_ID3),
                        new UpdateAuthorDTO(null, "Павлович", "Фёдор", null, null),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID3,
                                "Чехов",
                                "Павлович",
                                "Фёдор",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                LocalDate.of(1860, 1, 29),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление страны", AuthorUtils.AUTHOR_ID7),
                        new UpdateAuthorDTO(null, null, null, CountryUtils.COUNTRY_ID2, null),
                        new AuthorDTO(
                                AuthorUtils.AUTHOR_ID7,
                                "Мураками",
                                null,
                                "Харуки",
                                new CountryDTO(CountryUtils.COUNTRY_ID2, null),
                                LocalDate.of(1949, 1, 12),
                                null,
                                null
                        )
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное обновление")
    void updateSuccessTest(UUID authorId, UpdateAuthorDTO dto, AuthorDTO expected) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateAuthorDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<AuthorDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + authorId,
                HttpMethod.PUT,
                httpEntity,
                AuthorDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        AuthorDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expected.getId(), body.getId());
        Assertions.assertEquals(expected.getLastName(), body.getLastName());
        Assertions.assertEquals(expected.getMiddleName(), body.getMiddleName());
        Assertions.assertEquals(expected.getFirstName(), body.getFirstName());
        Assertions.assertEquals(expected.getCountry().getId(), body.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - автор по идентификатору не найден")
    void updateAuthorNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateAuthorDTO> httpEntity = new HttpEntity<>(new UpdateAuthorDTO(), headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + authorId,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(AuthorByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - страна по идентификатору не найден")
    void updateCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdateAuthorDTO dto = new UpdateAuthorDTO(null, null, null, countryId, null);

        HttpEntity<UpdateAuthorDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + AuthorUtils.AUTHOR_ID7,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - пользователь не является администратором")
    void updateUserNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateAuthorDTO> httpEntity = new HttpEntity<>(new UpdateAuthorDTO(), headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + AuthorUtils.AUTHOR_ID5,
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

        ResponseEntity<AuthorDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + AuthorUtils.AUTHOR_ID4,
                HttpMethod.GET,
                httpEntity,
                AuthorDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        AuthorDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(AuthorUtils.AUTHOR_ID4, body.getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - автор по идентификатору не найден")
    void getByIdNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + authorId,
                HttpMethod.GET,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(AuthorByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccess() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + AuthorUtils.AUTHOR_ID2,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        Assertions.assertEquals(6, authorRepository.findAll().size());
    }

    @Test
    @DisplayName("Неудачное удаление - автор по идентификатору не найден")
    void deleteNotFoundFailTest() {
        UUID authorId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + authorId,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(AuthorByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное удаление - пользователь не является администратором")
    void deleteUserNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + AuthorUtils.AUTHOR_ID7,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    private static Stream<Arguments> findAllTest() {
        return Stream.of(
                arguments(named("Поиск по фамилии", "СтО"), null, null, null, null, null, 2),
                arguments(named("Поиск по имени", "а"), null, null, null, null, null, 5),
                arguments(named("Поиск по отчеству", "ОвИч"), null, null, null, null, null, 2),
                arguments(named("Поиск по полному фио", "Сергеевич Пушкин Александр"), null, null, null, null, null, 1),
                arguments(named("Поиск по краткому фио", "А С Пушкин"), null, null, null, null, null, 1),
                arguments(named("Поиск по стране", null), "ИТАЙ", null, null, null, null, 2),
                arguments(named("Поиск с пустыми названиями", ""), "", null, null, null, null, 7),
                arguments(named("Поиск без названий", null), null, null, null, null, null, 7),
                arguments(named("Поиск по дате рождения 1", null), null, LocalDate.of(1900, 2, 3), null, null, null, 2),
                arguments(named("Поиск по дате рождения 2", null), null, null, LocalDate.of(1881, 9, 25), null, null, 5),
                arguments(named("Поиск по дате рождения 3", null), null, LocalDate.of(1860, 1, 29),  LocalDate.of(2010, 11, 25), null, null, 4),
                arguments(named("Пагинация 1", null), null, null, null, 1, 3, 3),
                arguments(named("Пагинация 2", null), null, null, null, 0, 4, 4)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка авторов с фильтрами")
    void findAllTest(
            String fio,
            String countryName,
            LocalDate startBirthday,
            LocalDate endBirthday,
            Integer page,
            Integer perPage,
            long expectedSize
    ) {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        var urlBuilder = UrlUtils.builder(BASE_URL);

        if (fio != null)
            urlBuilder.add("fio", fio);
        if (countryName != null)
            urlBuilder.add("country_name", countryName);
        if (page != null)
            urlBuilder.add("page", page);
        if (perPage != null)
            urlBuilder.add("per_page", perPage);
        if (startBirthday != null)
            urlBuilder.add("start_birthday", startBirthday);
        if (endBirthday != null)
            urlBuilder.add("end_birthday", endBirthday);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<AuthorListDTO> actual = restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                AuthorListDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        AuthorListDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedSize, body.getData().size());
    }
}

