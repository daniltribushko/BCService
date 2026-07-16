package ru.tdd.geo.integrations.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.utils.TextUtils;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.city.CreateCityDTO;
import ru.tdd.geo.application.models.dto.geo.city.UpdateCityDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityAlreadyExistException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.utils.URLUtils;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.sql.InitCitiesSqlScrips;
import ru.tdd.geo.utils.CityUtils;
import ru.tdd.geo.utils.CountryUtils;
import ru.tdd.geo.utils.RegionUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 20.01.2026
 * Набор тестов для контроллера городов
 */
@Testcontainers
@SpringBootTest
@InitCitiesSqlScrips
@AutoConfigureMockMvc
@DisplayName("Тест контроллера городов")
@Import(value = TestcontainersConfiguration.class)
class CityControllerTest {

    private final MockMvc mockMvc;

    private final CityRepository cityRepository;

    private final ObjectMapper objectMapper;

    private static final String BASE_URL = "/geo/cities";

    @Autowired
    CityControllerTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            CityRepository cityRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.cityRepository = cityRepository;
    }

    @Test
    @DisplayName("Удачное создание")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createSuccessTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new CreateCityDTO(
                                                "Чебоксары",
                                                RegionUtils.REGION_ID1,
                                                null
                                        )
                                )
                        )
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Чебоксары")))
                .andExpect(jsonPath("$.region.id", is(RegionUtils.REGION_ID1.toString())))
                .andExpect(jsonPath("$.country.id", is(CountryUtils.COUNTRY_ID1.toString())));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Неудачное создание - пользователь не является администратором")
    void createNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new CreateCityDTO(
                                                "Fail Create City",
                                                UUID.randomUUID(),
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Неудачное создание - регион не найден")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createRegionNotFoundFailTest() throws Exception {
        UUID regionId = UUID.randomUUID();
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new CreateCityDTO(
                                                "Fail Create City",
                                                regionId,
                                                null
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(RegionByIdNotFoundException.getErrorText(regionId))));
    }

    @Test
    @DisplayName("Неудачное создание - город не найдено")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCityNotFoundFailTest() throws Exception {
        UUID countryId = UUID.randomUUID();
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new CreateCityDTO(
                                                "Fail Create City",
                                                null,
                                                countryId
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(CountryByIdNotFoundException.getErrorText(countryId))));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Не удачное создание - город уже создан")
    void createAlreadyExistsFailTest() throws Exception {
        ResultActions response1 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new CreateCityDTO(
                                                "Москва",
                                                null,
                                                CountryUtils.COUNTRY_ID1
                                        )
                                )
                        )
        );

        ResultActions response2 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new CreateCityDTO(
                                                "Одинцово",
                                                RegionUtils.REGION_ID1,
                                                null
                                        )
                                )
                        )
        );

        response1.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is(CityAlreadyExistException.getErrorText("Москва", CountryUtils.COUNTRY_ID1, null))
                        )
                );

        response2.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is(CityAlreadyExistException.getErrorText("Одинцово", CountryUtils.COUNTRY_ID1, RegionUtils.REGION_ID1))
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Неудачное создание - данные не валидны")
    void createNotValidFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new CreateCityDTO(
                                                "Test City",
                                                null,
                                                null
                                        )
                                )
                        )
        );

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Необходимо указать идентификатор региона или страны")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Удачное обновление")
    void updateSuccessTest() throws Exception {

        String url = BASE_URL + "/" + CityUtils.CITY_ID2;

        ResultActions response1 = mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new UpdateCityDTO(
                                                "Новый город",
                                                null,
                                                null
                                        )
                                )
                        )
        );

        ResultActions response2 = mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new UpdateCityDTO(
                                                null,
                                                RegionUtils.REGION_ID2,
                                                null
                                        )
                                )
                        )
        );

        ResultActions response3 = mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new UpdateCityDTO(
                                                null,
                                                null,
                                                CountryUtils.COUNTRY_ID2
                                        )
                                )
                        )
        );

        response1.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Новый город")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.region",
                                nullValue(RegionDTO.class)
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(CountryUtils.COUNTRY_ID1.toString())
                        )
                );

        response2.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Новый город")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.region.id",
                                is(RegionUtils.REGION_ID2.toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(CountryUtils.COUNTRY_ID2.toString())
                        )
                );

        response3.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Новый город")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.region",
                                nullValue(RegionDTO.class)
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(CountryUtils.COUNTRY_ID2)
                        )
                );
    }

    @Test
    @DisplayName("Неудачное обновление - пользователь не является администратором")
    void updateNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new UpdateCityDTO()
                                )
                        )
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Неудачное обновление - город не найден")
    void updateCityNotFoundFailTest() throws Exception {
        UUID cityId = UUID.randomUUID();
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + cityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new UpdateCityDTO()
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is(CityByIdNotFoundException.getErrorText(cityId))
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Неудачное обновление - регион не найден")
    void updateRegionNotFoundFailTest() throws Exception {
        UUID regionId = UUID.randomUUID();
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + CityUtils.CITY_ID4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new UpdateCityDTO(
                                                null,
                                                regionId,
                                                null
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is(RegionByIdNotFoundException.getErrorText(regionId))
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Не удачное обновление - страна не найдена")
    void updateCountryNotFoundFailTest() throws Exception {
        UUID countryId = UUID.randomUUID();
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + CityUtils.CITY_ID5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        new UpdateCityDTO(
                                                null,
                                                null,
                                                countryId
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is(CountryByIdNotFoundException.getErrorText(countryId))
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + CityUtils.CITY_ID10)
        );

        response.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(CityUtils.CITY_ID10.toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Харбин")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.region",
                                is(RegionUtils.REGION_ID5)
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(CountryUtils.COUNTRY_ID2)
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Неудачное получение по идентификатору - город не найден")
    void getByIdNotFoundFailTest() throws Exception {
        UUID cityId = UUID.randomUUID();
        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + cityId)
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is(CityByIdNotFoundException.getErrorText(cityId))
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + CityUtils.CITY_ID8)
        );

        long actualCount = cityRepository.count();

        response.andExpect(status().isNoContent());
        Assertions.assertEquals(10, actualCount);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Неудачное удаление - город не найден")
    void deleteNotFoundFailTest() throws Exception {
        UUID cityId = UUID.randomUUID();
        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + cityId)
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is(CityByIdNotFoundException.getErrorText(cityId))
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Неудачное удаление - пользователь не является администратором")
    void deleteNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + UUID.randomUUID())
        );

        response.andExpect(status().isForbidden());
    }

    private static Stream<Arguments> getAllTest() {
        return Stream.of(
/*                arguments(named("Поиск по названию города 1", "а"), null, null, null, null, 6),
                arguments(named("Поиск по названию города 2", "БНА"), null, null, null, null, 1),
                arguments(named("Поиск по названию города и региона 1", "А"), "ОвСк", null, null, null, 3),
                arguments(named("Поиск по названию города и региона 2", "ХЭФЭЙ"), "АНЬХОЙ", null, null, null, 1),
                arguments(named("Поиск по названиям города, региона, страны 1", "ДУБНА"), "московская область", "РОССИЯ", null, null, 1),
                arguments(named("Поиск по названиям города, региона, страны 2", "хэфэй"), "аньхой", "китай", null, null, 1),*/
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

        ResultActions actual = mockMvc.perform(get(urlBuilder.build()));
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(expectedSize)));
    }
}
