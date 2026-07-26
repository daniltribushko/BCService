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
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.TextUtils;
import ru.tdd.bc.utils.UrlUtils;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationListData;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationByIdNotFoundException;
import ru.tdd.geo.database.repositories.LocationRepository;
import ru.tdd.geo.sql.InitLocationsSqlScrips;
import ru.tdd.geo.utils.CityUtils;
import ru.tdd.geo.utils.LocationUtils;
import ru.tdd.geo.utils.UserUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 27.01.2026
 */
@Testcontainers
@InitLocationsSqlScrips
@DisplayName("Тест контроллера локаций")
@Import(value = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LocationControllerTest {

    private static final String BASE_URL = "/geo/locations";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("Успешное сохранение")
    void saveSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        CreateLocationDTO dto = new CreateLocationDTO("ВДНХ", CityUtils.CITY_ID4);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<LocationDTO> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                LocationDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        LocationDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getId());
        Assertions.assertEquals("ВДНХ", body.getName());
        Assertions.assertEquals(CityUtils.CITY_ID4, body.getCity().getId());
    }

    @Test
    @DisplayName("Неудачное сохранение - пользователь не является администратором")
    void saveNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        CreateLocationDTO dto = new CreateLocationDTO("Тест", CityUtils.CITY_ID1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное сохранение - город не найден")
    void saveCityNoyFoundFailTest() {
        UUID cityId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        CreateLocationDTO dto = new CreateLocationDTO("Тест", cityId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(CityByIdNotFoundException.getErrorText(cityId), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное сохранение - локация уже создана")
    void saveAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        CreateLocationDTO dto = new CreateLocationDTO("Эрмитаж", CityUtils.CITY_ID5);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<CreateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(LocationAlreadyExistsException.getErrorText("Эрмитаж", CityUtils.CITY_ID5), body.getMessage());
    }

    private static Stream<Arguments> updateSuccessTest() {
        return Stream.of(
                arguments(
                        named(
                                "Обновление названия",
                                LocationUtils.LOCATION_ID2
                        ),
                        new UpdateLocationDTO(
                                "ВДНХ",
                                null
                        ),
                        "ВДНХ",
                        CityUtils.CITY_ID4
                ),
                arguments(
                        named(
                                "Обновление города",
                                LocationUtils.LOCATION_ID6
                        ),
                        new UpdateLocationDTO(
                                null,
                                CityUtils.CITY_ID10
                        ),
                        "Площадь Святого Петра",
                        CityUtils.CITY_ID10
                ),
                arguments(
                        named(
                                "Обновление города и названия",
                                LocationUtils.LOCATION_ID4
                        ),
                        new UpdateLocationDTO(
                                "Мавзолей",
                                CityUtils.CITY_ID4
                        ),
                        "Мавзолей",
                        CityUtils.CITY_ID4
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное обновление")
    void updateSuccessTest(UUID id, UpdateLocationDTO dto, String expectedName, UUID expectedCityId) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<LocationDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                httpEntity,
                LocationDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        LocationDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(id, body.getId());
        Assertions.assertEquals(expectedName, body.getName());
        Assertions.assertEquals(expectedCityId, body.getCity().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - пользователь не является администратором")
    void updateNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        UpdateLocationDTO dto = new UpdateLocationDTO(null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + LocationUtils.LOCATION_ID6,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное обновление - локация не найдена")
    void updateLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        UpdateLocationDTO dto = new UpdateLocationDTO();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + locationId,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(LocationByIdNotFoundException.getErrorText(locationId), body.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - город не найден")
    void updateCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        UpdateLocationDTO dto = new UpdateLocationDTO(null, cityId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + LocationUtils.LOCATION_ID2,
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
    @DisplayName("Неудачное обновление - локация уже создана")
    void updateAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        UpdateLocationDTO dto = new UpdateLocationDTO("Эрмитаж", CityUtils.CITY_ID5);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateLocationDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + LocationUtils.LOCATION_ID2,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(LocationAlreadyExistsException.getErrorText("Эрмитаж", CityUtils.CITY_ID5), body.getMessage());
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
                BASE_URL + "/" + LocationUtils.LOCATION_ID3,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        Assertions.assertEquals(5, locationRepository.findAll().size());
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
                BASE_URL + "/" + LocationUtils.LOCATION_ID2,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное удаление - локация не найдена")
    void deleteNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + locationId,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(LocationByIdNotFoundException.getErrorText(locationId), body.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdSuccessTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<LocationDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + LocationUtils.LOCATION_ID1,
                HttpMethod.GET,
                httpEntity,
                LocationDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        LocationDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(LocationUtils.LOCATION_ID1, body.getId());
        Assertions.assertEquals("Российская государственная библиотека", body.getName());
        Assertions.assertEquals(CityUtils.CITY_ID4, body.getCity().getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - локация не найдена")
    void findByIdNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + locationId,
                HttpMethod.GET,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(LocationByIdNotFoundException.getErrorText(locationId), body.getMessage());
    }

    private static Stream<Arguments> findAllTest1() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "пЛоЩаДь"), null, null, null, 2),
                arguments(named("Поиск по названию 2", "эРмИт"), null, null, null, 1),
                arguments(named("Поиск по названию города 1", null), "ПЕТЕРБУРГ", null, null, 3),
                arguments(named("Поиск по названию города 2", null), "москва", null, null, 2),
                arguments(named("Поиск по названию и названию города 1", "САД"), "Москва", null, null, 1),
                arguments(named("Поиск по названию и названию города 2", "Эрмитаж"), "Санкт", null, null, 1),
                arguments(named("Поиск с пустыми названиями", ""), "", null, null, 6),
                arguments(named("Поиск без названий", null), null, null, null, 6),
                arguments(named("Пагинация 1", null), null, 1, 3, 3),
                arguments(named("Пагинация 2", null), null, 1, 5, 1)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка с ограниченным набором фильтров")
    void findAllTest1(String name, String cityName, Integer page, Integer perPage, int expectedSize) {
        var urlBuilder = UrlUtils.builder(BASE_URL);

        if (!TextUtils.isEmpty(name))
            urlBuilder.add("name", name);
        if (!TextUtils.isEmpty(cityName))
            urlBuilder.add("city_name", cityName);
        if (page != null)
            urlBuilder.add("page", page);
        if (perPage != null)
            urlBuilder.add("per_page", perPage);

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<LocationListData> actual = restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                LocationListData.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        LocationListData body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedSize, body.getData().size());
    }

    @Test
    @DisplayName("Получение списка с полным набором фильтров")
    void findAllTest2() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        String url = UrlUtils.builder(BASE_URL)
                .add("name", "ПлощАдь")
                .add("region_name", "")
                .add("city_name", "Санкт-Петербург")
                .add("country_name", "РОССИЯ")
                .build();

        ResponseEntity<LocationListData> actual = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                LocationListData.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        LocationListData body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(1, body.getData().size());
    }
}
