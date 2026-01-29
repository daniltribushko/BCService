package ru.tdd.geo.integrations.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.DTOMapper;
import ru.tdd.geo.application.models.dto.geo.city.CreateCityDTO;
import ru.tdd.geo.application.models.dto.geo.city.UpdateCityDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.application.utils.URLUtils;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
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
@AutoConfigureMockMvc
@ImportTestcontainers(value = TestcontainersConfiguration.class)
class CityControllerTest {

    private final MockMvc mockMvc;

    private final CountryRepository countryRepository;

    private final RegionRepository regionRepository;

    private final CityRepository cityRepository;

    private static final String BASE_URL = "/geo/cities";

    @Autowired
    CityControllerTest(
            MockMvc mockMvc,
            CountryRepository countryRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.mockMvc = mockMvc;
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.cityRepository = cityRepository;
    }

    @BeforeEach
    void cleanDb() {
        cityRepository.deleteAll();
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createSuccessTest() throws Exception {
        Country country = new Country("Russia");

        countryRepository.save(country);

        Region region = new Region("Moscow Oblast", country);

        regionRepository.save(region);

        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new CreateCityDTO("Moscow", region.getId(), null)))
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Moscow")))
                .andExpect(jsonPath("$.region.id", is(region.getId().toString())))
                .andExpect(jsonPath("$.country.id", is(country.getId().toString())));
    }

    @Test
    @WithMockUser(username = "user")
    void createNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
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
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createRegionNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateCityDTO(
                                                "Fail Create City",
                                                UUID.randomUUID(),
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Регион с указанным идентификатором не найден")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCityNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateCityDTO(
                                                "Fail Create City",
                                                null,
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Страна с указанным идентификатором не найдена")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createAlreadyExistsFailTest() throws Exception {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        Region region = new Region("Test Region", country);

        regionRepository.save(region);

        City city1 = new City("Test City 1", null, country);
        City city2 = new City("Test City 2", region, country);

        cityRepository.saveAll(List.of(city1, city2));

        ResultActions response1 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateCityDTO(
                                                "Test City 1",
                                                null,
                                                country.getId()
                                        )
                                )
                        )
        );

        ResultActions response2 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateCityDTO(
                                                "Test City 2",
                                                region.getId(),
                                                null
                                        )
                                )
                        )
        );

        response1.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Город с указанным названием, страной, регионом уже создан")
                        )
                );

        response2.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Город с указанным названием, страной, регионом уже создан")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createNotValidFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
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
    void updateSuccessTest() throws Exception {
        Country country1 = new Country("Test Country 1");
        Country country2 = new Country("Test Country 2");
        Country country3 = new Country("Test Country 3");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Region region = new Region("Test Region", country2);

        regionRepository.save(region);

        City city = new City("City", null, country1);

        cityRepository.save(city);

        String url = BASE_URL + "/" + city.getId();

        ResultActions response1 = mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateCityDTO(
                                                "Updated City",
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
                                DTOMapper.toJson(
                                        new UpdateCityDTO(
                                                null,
                                                region.getId(),
                                                null
                                        )
                                )
                        )
        );

        ResultActions response3 = mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateCityDTO(
                                                null,
                                                null,
                                                country3.getId()
                                        )
                                )
                        )
        );

        response1.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Updated City")
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
                                is(country1.getId().toString())
                        )
                );

        response2.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Updated City")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.region.id",
                                is(region.getId().toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(country2.getId().toString())
                        )
                );

        response3.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Updated City")
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
                                is(country3.getId().toString())
                        )
                );
    }

    @Test
    void updateNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateCityDTO()
                                )
                        )
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCityNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateCityDTO()
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Город с указанным идентификатором не найден")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRegionNotFoundFailTest() throws Exception {
        Country country = new Country("Test Country");

        countryRepository.save(country);


        City city = new City("Test City", null, country);

        cityRepository.save(city);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + city.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateCityDTO(
                                                null,
                                                UUID.randomUUID(),
                                                null
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Регион с указанным идентификатором не найден")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCountryNotFoundFailTest() throws Exception {
        Country country = new Country("Fail Test Country 2");

        countryRepository.save(country);

        City city = new City("Fail Test City 2", null, country);

        cityRepository.save(city);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + city.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateCityDTO(
                                                null,
                                                null,
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Страна с указанным идентификатором не найдена")
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    void getByIdSuccessTest() throws Exception {
        Country country = new Country("Test Get City By Id Country");

        countryRepository.save(country);

        City city = new City("Test Get City By Id", null, country);

        cityRepository.save(city);

        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + city.getId())
        );

        response.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(city.getId().toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.name",
                                is("Test Get City By Id")
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
                                is(country.getId().toString())
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    void getByIdNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Город с указанным идентификатором не найден")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteSuccessTest() throws Exception {
        Country country = new Country("Test Delete City Country");

        countryRepository.save(country);

        City city = new City("Test Delete City", null, country);

        cityRepository.save(city);

        long expectedCount = cityRepository.count() - 1;

        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + city.getId())
        );

        long actualCount = cityRepository.count();

        response.andExpect(status().isNoContent());
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Город с указанным идентификатором не найден")
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    void deleteNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + UUID.randomUUID())
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void getAllTest() throws Exception {
        Country country1 = new Country("tEsT");
        Country country2 = new Country("testING");
        Country country3 = new Country("cOuNtRy");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Region region1 = new Region("Test Region 1", country1);
        Region region2 = new Region("rEgIoN", country3);

        regionRepository.saveAll(List.of(region1, region2));

        City city1 = new City("cItY", null, country1);
        City city2 = new City("TesT ciTY", null, country1);
        City city3 = new City("tEsTiNg", region1, country2);
        City city4 = new City("TEST", region1, country2);
        City city5 = new City("Moscow", region2, country3);
        City city6 = new City("COW", region2, country3);

        cityRepository.saveAll(List.of(city1, city2, city3, city4, city5, city6));

        ResultActions response1 = mockMvc.perform(
                get(
                        URLUtils.builder(BASE_URL)
                                .addQueryParameter("name", "cIt")
                                .addQueryParameter("country-name", "EsT")
                                .build()
                )
        );

        ResultActions response2 = mockMvc.perform(
                get(
                        URLUtils.builder(BASE_URL)
                                .addQueryParameter("region-name", "rEg")
                                .build()
                )
        );

        ResultActions response3 = mockMvc.perform(
                get(
                        URLUtils.builder(BASE_URL)
                                .addQueryParameter("name", "Cow")
                                .build()
                )
        );

        ResultActions response4 = mockMvc.perform(
                get(BASE_URL)
        );

        ResultActions response5 = mockMvc.perform(
                get(
                        URLUtils.builder(BASE_URL)
                                .addQueryParameter("page", 1)
                                .addQueryParameter("per-page", 4)
                                .build()
                )
        );

        response1.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
        response2.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(4)));
        response3.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
        response4.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(6)));
        response5.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }
}
