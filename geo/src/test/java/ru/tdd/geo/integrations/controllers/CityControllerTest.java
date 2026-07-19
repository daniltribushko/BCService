package ru.tdd.geo.integrations.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.TextUtils;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.application.models.dto.geo.city.CityListData;
import ru.tdd.geo.application.models.dto.geo.city.CreateCityDTO;
import ru.tdd.geo.application.models.dto.geo.city.UpdateCityDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityAlreadyExistException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.utils.URLUtils;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.sql.InitCitiesSqlScrips;
import ru.tdd.geo.utils.CityUtils;
import ru.tdd.geo.utils.CountryUtils;
import ru.tdd.geo.utils.RegionUtils;
import ru.tdd.geo.utils.UserUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 20.01.2026
 * Набор тестов для контроллера городов
 */
@Testcontainers
@InitCitiesSqlScrips
@DisplayName("Тест контроллера городов")
@Import(value = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CityControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final String BASE_URL = "/geo/cities";

    @Test
    @DisplayName("Удачное создание")
    void createSuccessTest() {
        CreateCityDTO dto = new CreateCityDTO("Коломна", RegionUtils.REGION_ID1, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtService.generateToken(UserUtils.ADMIN, secretKey));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<CityDTO> actual = restTemplate.postForEntity(
                BASE_URL,
                httpEntity,
                CityDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        CityDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getId());
        Assertions.assertEquals("Коломна", body.getName());
        Assertions.assertEquals(RegionUtils.REGION_ID1, body.getRegion().getId());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, body.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное создание - пользователь не является администратором")
    void createNotAdminFailTest() {
        CreateCityDTO dto = new CreateCityDTO("Сургут", null, CountryUtils.COUNTRY_ID1);

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.postForEntity(
                BASE_URL,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное создание - регион не найден")
    void createRegionNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();

        CreateCityDTO dto = new CreateCityDTO("Тест", regionId, null);

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.postForEntity(
                BASE_URL,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(RegionByIdNotFoundException.getErrorText(regionId), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное создание - страна не найдена")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCityNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        CreateCityDTO dto = new CreateCityDTO("Тест", null, countryId);

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateCityDTO> httpEntity = new HttpEntity<>(dto, headers);
        ResponseEntity<ExceptionDto> actual = restTemplate.postForEntity(
                BASE_URL,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), body.getMessage());
    }

    private static Stream<Arguments> createAlreadyExistsFailTest() {
        return Stream.of(
                arguments(
                        named(
                                "Создание через регион",
                                new CreateCityDTO("Одинцово", RegionUtils.REGION_ID1, null)
                        ),
                        CityAlreadyExistException.getErrorText("Одинцово", CountryUtils.COUNTRY_ID1, RegionUtils.REGION_ID1)
                ),
                arguments(
                        named(
                                "Создание через страну",
                                new CreateCityDTO("Москва", null, CountryUtils.COUNTRY_ID1)
                        ),
                        CityAlreadyExistException.getErrorText("Москва", CountryUtils.COUNTRY_ID1, null)
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Не удачное создание - город уже создан")
    void createAlreadyExistsFailTest(CreateCityDTO dto, String errorMessage) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.postForEntity(
                BASE_URL,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(errorMessage, body.getMessage());
    }

    @Test
    @DisplayName("Неудачное создание - данные не валидны")
    void createNotValidFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        CreateCityDTO dto = new CreateCityDTO("Сургут", null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.postForEntity(
                BASE_URL,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals("Необходимо указать идентификатор региона или страны", body.getMessage());
    }

    private static Stream<Arguments> updateSuccessTest() {
        return Stream.of(
                arguments(
                        named(
                                "Обновление названия",
                                CityUtils.CITY_ID3
                        ),
                        new UpdateCityDTO("Сургут", null, null),
                        "Сургут",
                        RegionUtils.REGION_ID1,
                        CountryUtils.COUNTRY_ID1
                ),
                arguments(
                        named(
                                "Обновление региона",
                                CityUtils.CITY_ID11
                        ),
                        new UpdateCityDTO(null, RegionUtils.REGION_ID1, null),
                        "Рим",
                        RegionUtils.REGION_ID1,
                        CountryUtils.COUNTRY_ID4
                ),
                arguments(
                        named(
                                "Обновление страны",
                                CityUtils.CITY_ID7
                        ),
                        new UpdateCityDTO(null, null, CountryUtils.COUNTRY_ID2),
                        "Екатеринбург",
                        null,
                        CountryUtils.COUNTRY_ID2
                ),
                arguments(
                        named(
                                "Обновление названия и региона",
                                CityUtils.CITY_ID8
                        ),
                        new UpdateCityDTO("Брянск", RegionUtils.REGION_ID5, null),
                        "Брянск",
                        RegionUtils.REGION_ID5,
                        CountryUtils.COUNTRY_ID2
                ),
                arguments(
                        named(
                                "Обновление названия и страны",
                                CityUtils.CITY_ID11
                        ),
                        new UpdateCityDTO("Кемерово", null, CountryUtils.COUNTRY_ID1),
                        "Кемерово",
                        null,
                        CountryUtils.COUNTRY_ID1
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное обновление")
    void updateSuccessTest(UUID cityId, UpdateCityDTO dto, String expectedName, UUID expectedRegionId, UUID expectedCountryId) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<CityDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + cityId,
                HttpMethod.PUT,
                httpEntity,
                CityDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CityDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedName, body.getName());
        Assertions.assertEquals(expectedCountryId, body.getCountry().getId());

        if (expectedRegionId != null)
            Assertions.assertEquals(body.getRegion().getId(), expectedRegionId);
        else
            Assertions.assertNull(body.getRegion());
    }

    @Test
    @DisplayName("Неудачное обновление - пользователь не является администратором")
    void updateNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdateCityDTO dto = new UpdateCityDTO();

        HttpEntity<UpdateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + CityUtils.CITY_ID3,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное обновление - город не найден")
    void updateCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateCityDTO> httpEntity = new HttpEntity<>(new UpdateCityDTO(), headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + cityId,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CityByIdNotFoundException.getErrorText(cityId), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - регион не найден")
    void updateRegionNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateCityDTO> httpEntity = new HttpEntity<>(
                new UpdateCityDTO(null, regionId, null),
                headers
        );

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + CityUtils.CITY_ID1,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(RegionByIdNotFoundException.getErrorText(regionId), body.getMessage());
    }

    @Test
    @DisplayName("Не удачное обновление - страна не найдена")
    void updateCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdateCityDTO dto = new UpdateCityDTO(null, null, countryId);

        HttpEntity<UpdateCityDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + CityUtils.CITY_ID5,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());


    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<CityDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + CityUtils.CITY_ID3,
                HttpMethod.GET,
                httpEntity,
                CityDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CityDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CityUtils.CITY_ID3, body.getId());
        Assertions.assertEquals("Одинцово", body.getName());
        Assertions.assertEquals(RegionUtils.REGION_ID1, body.getRegion().getId());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, body.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - город не найден")
    void getByIdNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + cityId,
                HttpMethod.GET,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CityByIdNotFoundException.getErrorText(cityId), body.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> actual = restTemplate.exchange(
                BASE_URL + "/" + CityUtils.CITY_ID8,
                HttpMethod.DELETE,
                httpEntity,
                Object.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное удаление - город не найден")
    void deleteNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ExceptionDto> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + cityId,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CityByIdNotFoundException.getErrorText(cityId), body.getMessage());
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
                BASE_URL + "/" + CityUtils.CITY_ID5,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    private static Stream<Arguments> getAllTest() {
        return Stream.of(
                arguments(named("Поиск по названию города 1", "а"), null, null, null, null, 7),
                arguments(named("Поиск по названию города 2", "БНА"), null, null, null, null, 1),
                arguments(named("Поиск по названию города и региона 1", "А"), "ОвСк", null, null, null, 3),
                arguments(named("Поиск по названию города и региона 2", "ХЭФЭЙ"), "АНЬХОЙ", null, null, null, 1),
                arguments(named("Поиск по названиям города, региона, страны 1", "ДУБНА"), "московская область", "РОССИЯ", null, null, 1),
                arguments(named("Поиск по названиям города, региона, страны 2", "хэфэй"), "аньхой", "китай", null, null, 1),
                arguments(named("Поиск по названию города и страны", "МОСКВА"), null, "РОССИЯ", null, null, 1),
                arguments(named("Поиск с пустыми названиями", ""), "", "", null, null, 11),
                arguments(named("Поиск без названий", null), null, null, null, null, 11),
                arguments(named("Пагинация 1", null), null, null, 0, 5, 5),
                arguments(named("Пагинация 2", null), null, null, 3, 3, 2)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @WithMockUser(username = "user")
    @DisplayName("Получение списка с фильтрами")
    void getAllTest(String name, String regionName, String countryName, Integer page, Integer perPage, int expectedSize) throws Exception {
        var urlBuilder = URLUtils.builder(BASE_URL);

        if (!TextUtils.isEmpty(name))
            urlBuilder.addQueryParameter("name", name);
        if (!TextUtils.isEmpty(regionName))
            urlBuilder.addQueryParameter("region_name", regionName);
        if (!TextUtils.isEmpty(countryName))
            urlBuilder.addQueryParameter("country_name", countryName);
        if (page != null)
            urlBuilder.addQueryParameter("page", page);
        if (perPage != null)
            urlBuilder.addQueryParameter("per_page", perPage);

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<CityListData> actual = restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                CityListData.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        CityListData body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedSize, body.getData().size());
    }
}
