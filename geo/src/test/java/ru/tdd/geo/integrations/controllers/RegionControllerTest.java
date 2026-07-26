package ru.tdd.geo.integrations.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.TextUtils;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.region.CreateRegionDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionListData;
import ru.tdd.geo.application.models.dto.geo.region.UpdateRegionDTO;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.utils.URLUtils;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.sql.InitRegionsSqlScripts;
import ru.tdd.geo.utils.CountryUtils;
import ru.tdd.geo.utils.RegionUtils;
import ru.tdd.geo.utils.UserUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 10.01.2026
 * Набор тестов контроллера по работе с регионами
 */
@Testcontainers
@InitRegionsSqlScripts
@DisplayName(value = "Тест контроллера регионов")
@Import(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegionControllerTest {

    private static final String BASE_URL = "/geo/regions";

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    @DisplayName("Удачное сохранение")
    void saveSuccessTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateRegionDTO dto = new CreateRegionDTO("Тюменская область", CountryUtils.COUNTRY_ID1);

        HttpEntity<CreateRegionDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<RegionDTO> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                RegionDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, actual.getStatusCode());

        RegionDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getId());
        Assertions.assertEquals("Тюменская область", body.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, body.getCountry().getId());
    }

    @Test
    @DisplayName("Не удачное сохранение - пользователь не является администратором")
    void saveNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateRegionDTO dto = new CreateRegionDTO("Тест", CountryUtils.COUNTRY_ID1);

        HttpEntity<CreateRegionDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Неудачное сохранение - Регион уже создан")
    void saveAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateRegionDTO dto = new CreateRegionDTO("Московская область", CountryUtils.COUNTRY_ID1);

        HttpEntity<CreateRegionDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(
                RegionAlreadyExistsException.getErrorText("Московская область", CountryUtils.COUNTRY_ID1),
                body.getMessage()
        );
    }

    private static Stream<Arguments> updateSuccessTest() {
        return Stream.of(
                arguments(
                        named(
                                "Обновление названия",
                                RegionUtils.REGION_ID2
                        ),
                        new UpdateRegionDTO("Брянская область", null),
                        "Брянская область",
                        CountryUtils.COUNTRY_ID1
                ),
                arguments(
                        named(
                                "Обновление страны",
                                RegionUtils.REGION_ID5
                        ),
                        new UpdateRegionDTO(null, CountryUtils.COUNTRY_ID3),
                        "Хэйлунцзян",
                        CountryUtils.COUNTRY_ID3
                ),
                arguments(
                        named(
                                "Обновление названия и страны",
                                RegionUtils.REGION_ID6
                        ),
                        new UpdateRegionDTO("Тюменская область", CountryUtils.COUNTRY_ID1),
                        "Тюменская область",
                        CountryUtils.COUNTRY_ID1
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное обновление")
    void updateSuccessTest(UUID regionId, UpdateRegionDTO dto, String expectedName, UUID expectedCountryId) {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateRegionDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<RegionDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + regionId,
                HttpMethod.PUT,
                httpEntity,
                RegionDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());


        RegionDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(regionId, body.getId());
        Assertions.assertEquals(expectedName, body.getName());
        Assertions.assertEquals(expectedCountryId, body.getCountry().getId());
    }

    @Test
    @WithMockUser(username = "user")
    void updateNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateLocationDTO> httpEntity = new HttpEntity<>(new UpdateLocationDTO(), headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + RegionUtils.REGION_ID5,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Не удачное обновление - регион не найден")
    void updateRegionNotFoundTest() {
        UUID regionId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UpdateRegionDTO> httpEntity = new HttpEntity<>(new UpdateRegionDTO(), headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + regionId,
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
    void updateCountryNotFoundTest() {
        UUID countryId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdateRegionDTO dto = new UpdateRegionDTO(null, countryId);

        HttpEntity<UpdateRegionDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + RegionUtils.REGION_ID2,
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
    @DisplayName("Не удачное обновление - регион уже создан")
    void updateAlreadyExistsFailTest() {
        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UpdateRegionDTO dto = new UpdateRegionDTO("Московская область", CountryUtils.COUNTRY_ID1);

        HttpEntity<UpdateRegionDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + RegionUtils.REGION_ID6,
                HttpMethod.PUT,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(RegionAlreadyExistsException.getErrorText("Московская область", CountryUtils.COUNTRY_ID1), body.getMessage());
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
                BASE_URL + "/" + RegionUtils.REGION_ID3,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        Assertions.assertEquals(5, regionRepository.findAll().size());
    }

    @Test
    @DisplayName("Не удачное удаление - пользователь не является администратором")
    void deleteNotAdminFailTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> actual = restTemplate.exchange(
                BASE_URL + "/" + RegionUtils.REGION_ID2,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @DisplayName("Не удачное удаление - регион не найден")
    void deleteNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.ADMIN, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + regionId,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(RegionByIdNotFoundException.getErrorText(regionId), body.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdSuccessTest() {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<RegionDTO> actual = restTemplate.exchange(
                BASE_URL + "/" + RegionUtils.REGION_ID4,
                HttpMethod.GET,
                httpEntity,
                RegionDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        RegionDTO body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(RegionUtils.REGION_ID4, body.getId());
        Assertions.assertEquals("Аньхой", body.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, body.getCountry().getId());
    }

    @Test
    @DisplayName("Не удачное получение по идентификатору - регион не найден")
    void findByIdNotFoundFail() {
        UUID regionId = UUID.randomUUID();

        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ExceptionDto> actual = restTemplate.exchange(
                BASE_URL + "/" + regionId,
                HttpMethod.GET,
                httpEntity,
                ExceptionDto.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());

        ExceptionDto body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(RegionByIdNotFoundException.getErrorText(regionId), body.getMessage());
    }

    private static Stream<Arguments> findAllTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "ОБЛАСТЬ"), null, null, null, 3),
                arguments(named("Поиск по названию 2", "хой"), null, null, null, 1),
                arguments(named("Поиск по названию страны 1", null), "Китай", null, null, 2),
                arguments(named("Поиск по названию страны 2", null), "Россия", null, null, 3),
                arguments(named("Поиск по названию региона и страны 1", "оВсКаЯ"), "Россия", null, null, 2),
                arguments(named("Поиск по названию региона и страны 2", "Сицилия"), "Италия", null, null, 1),
                arguments(named("Поиск без названий", null), null, null, null, 6),
                arguments(named("Поиск с пустыми названиями", ""), "", null, null, 6),
                arguments(named("Пагинация 1", null), null, 5, 1, 1),
                arguments(named("Пагинация 2", null), null, 3, 2, 0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение с фильтрацией")
    void findAllTest(String name, String countryName, Integer page, Integer perPage, long expectedSize) {
        String token = jwtService.generateToken(UserUtils.USER, secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        var urlBuilder = URLUtils.builder(BASE_URL);

        if (!TextUtils.isEmpty(name))
            urlBuilder.addQueryParameter("name", name);
        if (!TextUtils.isEmpty(countryName))
            urlBuilder.addQueryParameter("country_name", countryName);
        if (page != null)
            urlBuilder.addQueryParameter("page", page);
        if (perPage != null)
            urlBuilder.addQueryParameter("per_page", perPage);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<RegionListData> actual = restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                RegionListData.class
        );

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());

        RegionListData body = actual.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedSize, body.getData().size());
    }
}
