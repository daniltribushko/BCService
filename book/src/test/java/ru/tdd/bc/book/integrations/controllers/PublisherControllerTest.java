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
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherListDTO;
import ru.tdd.bc.book.application.dto.publisher.UpdatePublisherDTO;
import ru.tdd.bc.book.application.exceptions.PublisherAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.PublisherByIdNotFoundException;
import ru.tdd.bc.book.database.repositories.PublisherRepository;
import ru.tdd.bc.book.sql.InitPublishersSqlScripts;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.book.utils.PublishersUtils;
import ru.tdd.bc.book.utils.UserUtils;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.UrlUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 07.08.2026
 */
@Testcontainers
@InitPublishersSqlScripts
@Import(TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест контроллера для работы с издателями")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PublisherControllerTest {

    private static final String BASE_URL = "/books/publishers";

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreatePublisherDTO dto = new CreatePublisherDTO(
                "АСТ",
                "https://ast.ru/",
                CountryUtils.COUNTRY_ID1
        );

        HttpEntity<CreatePublisherDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<PublisherDTO> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                PublisherDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        PublisherDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getId());
        Assertions.assertEquals("АСТ", body.getName());
        Assertions.assertEquals("https://ast.ru/", body.getUrl());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, body.getCountry().getId());
        Assertions.assertEquals(4, publisherRepository.count());
    }

    @Test
    @DisplayName("Неудачное создание - издатель уже создан")
    void saveAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreatePublisherDTO dto = new CreatePublisherDTO(
                "Вече",
                null,
                CountryUtils.COUNTRY_ID1
        );

        HttpEntity<CreatePublisherDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(PublisherAlreadyExistsException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное создание - страна не найдена")
    void saveCountryNotFoundFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UUID countryId = UUID.randomUUID();

        CreatePublisherDTO dto = new CreatePublisherDTO(
                "АСТ",
                null,
                countryId
        );

        HttpEntity<CreatePublisherDTO> httpEntity = new HttpEntity<>(dto, headers);

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

    private static Stream<Arguments> saveNotValidDtoFailTest() {
        return Stream.of(
                arguments(named("Не заполнено название", new CreatePublisherDTO("", null, UUID.randomUUID()))),
                arguments(named("Невалидный url", new CreatePublisherDTO("АСТ", "строка", UUID.randomUUID()))),
                arguments(named("Не указана страна", new CreatePublisherDTO("АСТ", null, null))),
                arguments(named("Пустой DTO", new CreatePublisherDTO("", null, null)))
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Неудачное создание - данные не валидны")
    void saveNotValidDtoFailTest(CreatePublisherDTO dto) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreatePublisherDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actual.getStatusCode());
    }

    @Test
    @DisplayName("Не удачное создание - пользователь не является администратором")
    void saveNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreatePublisherDTO dto = new CreatePublisherDTO("АСТ", null, UUID.randomUUID());

        HttpEntity<CreatePublisherDTO> httpEntity = new HttpEntity<>(dto, headers);

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
                        named("Обновление названия", PublishersUtils.PUBLISHER_ID1),
                        new UpdatePublisherDTO(
                                "АСТ",
                                "https://veche.ru",
                                null
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID1,
                                "АСТ",
                                "https://veche.ru",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, ""),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление url адреса", PublishersUtils.PUBLISHER_ID3),
                        new UpdatePublisherDTO(
                                null,
                                "https://ast.ru",
                                null
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID3,
                                "China Publishing Group",
                                "https://ast.ru",
                                new CountryDTO(CountryUtils.COUNTRY_ID2, null),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Обновление страны", PublishersUtils.PUBLISHER_ID3),
                        new UpdatePublisherDTO(
                                null,
                                "https://en.cnpubg.com",
                                CountryUtils.COUNTRY_ID3
                        ),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID3,
                                "China Publishing Group",
                                "https://en.cnpubg.com",
                                new CountryDTO(CountryUtils.COUNTRY_ID3, null),
                                null,
                                null
                        )
                ),
                arguments(
                        named("Удаление URL адреса", PublishersUtils.PUBLISHER_ID2),
                        new UpdatePublisherDTO(),
                        new PublisherDTO(
                                PublishersUtils.PUBLISHER_ID2,
                                "Питер",
                                null,
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                null,
                                null
                        )
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Успешное обновление")
    void updateSuccessTest(UUID id, UpdatePublisherDTO dto, PublisherDTO expected) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdatePublisherDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<PublisherDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                httpEntity,
                PublisherDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        PublisherDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(id, expected.getId());
        Assertions.assertEquals(expected.getName(), body.getName());
        Assertions.assertEquals(expected.getUrl(), body.getUrl());
        Assertions.assertEquals(expected.getCountry().getId(), body.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - издатель не найден")
    void updatePublisherNotFoundFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdatePublisherDTO> httpEntity = new HttpEntity<>(new UpdatePublisherDTO(), headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + UUID.randomUUID(),
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - издатель уже создан")
    void updateAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdatePublisherDTO dto = new UpdatePublisherDTO(
                "Питер",
                null,
                CountryUtils.COUNTRY_ID1
        );

        HttpEntity<UpdatePublisherDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + PublishersUtils.PUBLISHER_ID1,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(PublisherAlreadyExistsException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - страна не найдена")
    void updateCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdatePublisherDTO> httpEntity = new HttpEntity<>(
                new UpdatePublisherDTO(null, null, countryId),
                headers
        );

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + PublishersUtils.PUBLISHER_ID1,
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
    void updateNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdatePublisherDTO> httpEntity = new HttpEntity<>(new UpdatePublisherDTO(), headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + PublishersUtils.PUBLISHER_ID1,
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

        ResponseEntity<PublisherDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + PublishersUtils.PUBLISHER_ID3,
                HttpMethod.GET,
                httpEntity,
                PublisherDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        PublisherDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(PublishersUtils.PUBLISHER_ID3, body.getId());
        Assertions.assertEquals("China Publishing Group", body.getName());
        Assertions.assertEquals("https://en.cnpubg.com", body.getUrl());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, body.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - издатель не найден")
    void getByIdNotFoundFailTest() {
        UUID publisherId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + publisherId,
                HttpMethod.GET,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), body.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + PublishersUtils.PUBLISHER_ID2,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        Assertions.assertEquals(2, publisherRepository.count());
    }

    @Test
    @DisplayName("Неудачное обновление - издатель не найден")
    void deleteNotFoundFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + UUID.randomUUID(),
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(PublisherByIdNotFoundException.getErrorText(), body.getMessage());
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
                BASE_URL + "/" + PublishersUtils.PUBLISHER_ID2,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    private static Stream<Arguments> getAllTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "Е"), null, null, null, null, null, null, null, 2),
                arguments(named("Поиск по названию 2", "InG"), null, null, null, null, null, null, null, 1),
                arguments(named("Поиск по названию страны 1", null), "РОС", null, null, null, null, null, null, 2),
                arguments(named("Поиск по названию страны 2", null), "ай", null, null, null, null, null, null, 1),
                arguments(
                        named("Поиск по минимальной дате создания", null),
                        null,
                        LocalDateTime.of(2025, 1, 1, 1, 1),
                        null,
                        null,
                        null,
                        null,
                        null,
                        2
                ),
                arguments(
                        named("Поиск по максимальной дате создания", null),
                        null,
                        null,
                        LocalDateTime.of(2025, 3, 15, 10, 0, 0),
                        null,
                        null,
                        null,
                        null,
                        2
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка издателей с фильтрами")
    void getAllTest(
            String name,
            String countryName,
            LocalDateTime startCreationTime,
            LocalDateTime endCreationTime,
            LocalDateTime startUpdateTime,
            LocalDateTime endUpdateTime,
            Integer page,
            Integer perPage,
            long expectedSize
    ) {
        String url = UrlUtils.builder(BASE_URL)
                .add("name", name)
                .add("country_name", countryName)
                .add("start_creation_time", startCreationTime)
                .add("end_creation_time", endCreationTime)
                .add("start_update_time", startUpdateTime)
                .add("end_update_time", endUpdateTime)
                .add("page", page)
                .add("perPage", perPage)
                .build();

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<PublisherListDTO> actual = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                PublisherListDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        PublisherListDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(body.getData().size(), expectedSize);
    }
}
