package ru.tdd.geo.integrations.controllers;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.http.countries.CountryAlreadyExistsException;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.UrlUtils;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.application.models.dto.geo.country.CountryListData;
import ru.tdd.geo.application.models.dto.geo.country.CreateCountryDTO;
import ru.tdd.geo.application.models.dto.geo.country.UpdateCountryDTO;
import ru.tdd.geo.sql.InitCountriesSqlScripts;
import ru.tdd.geo.utils.CountryUtils;
import ru.tdd.geo.utils.UserUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Набор тестов контроллера стран
 */
@Testcontainers
@InitCountriesSqlScripts
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест контроллера стран")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CountryControllerTest {

    private static final String BASE_URL = "/geo/countries";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    @DisplayName("Удачное создание")
    void createTest() {
       String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

       CreateCountryDTO dto = new CreateCountryDTO("Австрия");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateCountryDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<CountryDTO> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                CountryDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        CountryDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getId());
        Assertions.assertEquals("Австрия", body.getName());
    }

    @Test
    @DisplayName("Не удачное создание - пользователь не является администратором")
    void createNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateCountryDTO> httpEntity = new HttpEntity<>(new CreateCountryDTO("Test"), headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Не удачное создание - страна уже создана")
    void createAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        CreateCountryDTO dto = new CreateCountryDTO("Россия");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateCountryDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryAlreadyExistsException.getErrorText("Россия"), body.getMessage());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        UpdateCountryDTO dto = new UpdateCountryDTO("СССР");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateCountryDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<CountryDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + CountryUtils.COUNTRY_ID1,
                HttpMethod.PUT,
                httpEntity,
                CountryDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CountryDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals("СССР", body.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, body.getId());
    }

    @Test
    @DisplayName("Не удачное обновление - пользователь не является администратором")
    void updateNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateCountryDTO> httpEntity = new HttpEntity<>(new UpdateCountryDTO(), headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + CountryUtils.COUNTRY_ID2,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Не удачное обновление - страна не найдена")
    void updateNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateCountryDTO> httpEntity = new HttpEntity<>(new UpdateCountryDTO(), headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + countryId,
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
    @DisplayName("Не удачное обновление - страна уже создана")
    void updateAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        UpdateCountryDTO dto = new UpdateCountryDTO("Россия");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateCountryDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + CountryUtils.COUNTRY_ID3,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryAlreadyExistsException.getErrorText("Россия"), body.getMessage());
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
               BASE_URL + "/" + CountryUtils.COUNTRY_ID3,
               HttpMethod.DELETE,
               httpEntity,
               String.class
       );

       Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    @DisplayName("Не удачное удаление - пользователь не является администратором")
    void deleteNotAdminFailTest() {
       String token = jwtService.generateToken(UserUtils.USER, secretKey);

       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.setBearerAuth(token);

       HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

       ResponseEntity<String> actual =  restTemplate.exchange(
               BASE_URL + "/" + CountryUtils.COUNTRY_ID1,
               HttpMethod.DELETE,
               httpEntity,
               String.class
       );

       Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Не удачное удаление - страна не найдена")
    void deleteNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + countryId,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), body.getMessage());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Удачное получение по идентификатору")
    void findByIdSuccessTest() {
       String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<CountryDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + CountryUtils.COUNTRY_ID2,
                HttpMethod.GET,
                httpEntity,
                CountryDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CountryDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals("Китай", body.getName());
    }

    @Test
    @DisplayName("Не удачное получение по идентификатору - страна не найдена")
    void findByIdNotFoundTest() {
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

    private static Stream<Arguments> findAllSuccessTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "ИЯ"), null, null, 3),
                arguments(named("Поиск по названию 2", "кИтАй"), null, null, 1),
                arguments(named("Поиск с пустым названием", ""), null, null, 4),
                arguments(named("Поиск без названия", null), null, null, 4),
                arguments(named("Пагинация 1", null),  1, 2, 2),
                arguments(named("Пагинация 2", null), 5, 10, 0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка с фильтрами")
    void findAllSuccessTest(String name, Integer page, Integer perPage, int expectedSize) {
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

        ResponseEntity<CountryListData> actual = restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                CountryListData.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CountryListData body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedSize, body.getData().size());
    }
}
