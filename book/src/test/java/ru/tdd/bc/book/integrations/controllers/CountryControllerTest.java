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
import ru.tdd.bc.book.application.dto.countries.CountryListDTO;
import ru.tdd.bc.book.sql.InitCountriesSqlScripts;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.book.utils.UserUtils;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.UrlUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Testcontainers
@InitCountriesSqlScripts
@DisplayName("Интеграционный тест контроллера стран")
@Import(value = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryControllerTest {

    private static final String BASE_URL = "/book/countries";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<CountryDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + CountryUtils.COUNTRY_ID1,
                HttpMethod.GET,
                httpEntity,
                CountryDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CountryDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, body.getId());
        Assertions.assertEquals("Россия", body.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - страна не найдена")
    void getByIdNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + countryId,
                HttpMethod.GET,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), body.getMessage());
    }

    private static Stream<Arguments> getAllTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "иЯ"), null, null, 2),
                arguments(named("Поиск по названию 2", "РОС"), null, null, 1),
                arguments(named("Поиск без названия", null), null, null, 3),
                arguments(named("Поиск с пустым названием", ""), null, null, 3),
                arguments(named("Пагинация 1", null), 1, 4, 0),
                arguments(named("Пагинация 2", null), 2, 1, 1)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка стран с фильтрами")
    void getAllTest(String name, Integer page, Integer perPage, long expectedSize) {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        var urlBuilder = UrlUtils.builder(BASE_URL);

        if (name != null)
            urlBuilder.add("name", name);
        if (page != null)
            urlBuilder.add("page", page);
        if (perPage != null)
            urlBuilder.add("per_page", perPage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<CountryListDTO> actual = restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                CountryListDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CountryListDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedSize, body.getData().size());
    }
}