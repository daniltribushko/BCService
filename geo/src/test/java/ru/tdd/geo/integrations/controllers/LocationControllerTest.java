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
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.utils.URLUtils;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Location;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.LocationRepository;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 27.01.2026
 */
@SpringBootTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureMockMvc
public class LocationControllerTest {

    private static final String BASE_URL = "/geo/locations";

    private final LocationRepository locationRepository;

    private final CityRepository cityRepository;

    private final CountryRepository countryRepository;

    private final MockMvc mockMvc;

    @Autowired
    LocationControllerTest(
            LocationRepository locationRepository,
            CityRepository cityRepository,
            CountryRepository countryRepository,
            MockMvc mockMvc
    ) {
        this.locationRepository = locationRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void cleanDb() {
        locationRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveSuccessTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateLocationDTO(
                                                "Test Location",
                                                city.getId()
                                        )
                                )
                        )
        );

        response.andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.name", is("Test Location"))
                )
                .andExpect(
                        jsonPath("$.city.id", is(city.getId().toString()))
                );
    }

    @Test
    @WithMockUser(username = "user")
    void saveNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateLocationDTO(
                                                "Not Admin",
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveCityNoyFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateLocationDTO(
                                                "",
                                                UUID.randomUUID()
                                        )
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
    void saveAlreadyExistsFailTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location = new Location("Test Location", city);

        locationRepository.save(location);

        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new CreateLocationDTO(
                                                "Test Location",
                                                city.getId()
                                        )
                                )
                        )
        );

        response.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Локация с указанным названием и городом уже создана")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateSuccessTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city1 = new City("Test City 1", null, country);
        City city2 = new City("Test City 2", null, country);

        cityRepository.saveAll(List.of(city1, city2));

        Location location1 = new Location("Location 1", city1);
        Location location2 = new Location("Location 2", city1);

        locationRepository.saveAll(List.of(location1, location2));

        ResultActions response1 = mockMvc.perform(
                put(BASE_URL + "/" + location1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateLocationDTO(
                                                "New Location",
                                                null
                                        )
                                )
                        )
        );

        ResultActions response2 = mockMvc.perform(
                put(BASE_URL + "/" + location2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateLocationDTO(
                                                "Updated Location",
                                                city2.getId()
                                        )
                                )
                        )
        );

        ResultActions response3 = mockMvc.perform(
                put(BASE_URL + "/" + location1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateLocationDTO(
                                                null,
                                                city2.getId()
                                        )
                                )
                        )
        );

        response1.andExpect(status().isOk());
        response2.andExpect(status().isOk());
        response3.andExpect(status().isOk());

        response1
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(location1.getId().toString())
                        )
                )
                .andExpect(
                        jsonPath("$.name", is("New Location"))
                );

        response2
                .andExpect(
                        jsonPath("$.id", is(location2.getId().toString()))
                )
                .andExpect(
                        jsonPath("$.name", is("Updated Location"))
                )
                .andExpect(
                        jsonPath("$.city.id", is(city2.getId().toString()))
                );

        response3
                .andExpect(
                        jsonPath("$.id", is(location1.getId().toString()))
                )
                .andExpect(
                        jsonPath("$.city.id", is(city2.getId().toString()))
                );
    }

    @Test
    @WithMockUser(username = "user")
    void updateNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateLocationDTO()
                                )
                        )
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateLocationNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateLocationDTO()
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Локация с указанным идентификатором не найдена")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCityNotFoundFailTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location = new Location("Location", city);

        locationRepository.save(location);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + location.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateLocationDTO(
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
                                is("Город с указанным идентификатором не найден")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateAlreadyExistsFailTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location1 = new Location("Location", city);
        Location location2 = new Location("Location 2", city);

        locationRepository.saveAll(List.of(location1, location2));

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + location1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateLocationDTO(
                                                "Location 2",
                                                city.getId()
                                        )
                                )
                        )
        );

        response.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Локация с указанным названием и городом уже создана")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteSuccessTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location = new Location("Location", city);

        locationRepository.save(location);

        long expectedCount = locationRepository.count() - 1;

        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + location.getId())
        );

        long actualCount = locationRepository.count();

        response.andExpect(status().isNoContent());
        Assertions.assertEquals(expectedCount, actualCount);
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
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Локация с указанным идентификатором не найдена")
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdSuccessTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location1 = new Location("Location 1", city);
        Location location2 = new Location("Location 2", city);
        Location location3 = new Location("Location 3", city);

        locationRepository.saveAll(List.of(location1, location2, location3));

        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + location2.getId())
        );

        response.andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id", is(location2.getId().toString()))
                );
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Локация с указанным идентификатором не найдена")
                        )
                );
    }

    @Test
    @WithMockUser(username = "user")
    void findAllTest() throws Exception {
        Country country = new Country("Test Country 1");

        countryRepository.save(country);

        City city1 = new City("ciTy", null, country);
        City city2 = new City("tEsT", null, country);
        City city3 = new City("qweTEScvb", null, country);

        cityRepository.saveAll(List.of(city1, city2, city3));

        Location location1 = new Location("TeSt LoCaTiOn", city1);
        Location location2 = new Location("LOCAtion", city1);
        Location location3 = new Location("TeStInG", city2);
        Location location4 = new Location("Moscow", city2);
        Location location5 = new Location("LOc", city2);
        Location location6 = new Location("COW", city3);

        locationRepository.saveAll(List.of(location1, location2, location3, location4, location5, location6));

        ResultActions response1 = mockMvc.perform(
                get(
                        URLUtils.builder(BASE_URL)
                                .addQueryParameter("name", "eSt")
                                .addQueryParameter("city-name", "eSt")
                                .build()
                )
        );

        ResultActions response2 = mockMvc.perform(
                get(
                        URLUtils.builder(BASE_URL)
                                .addQueryParameter("name", "oC")
                                .build()
                )
        );

        ResultActions response3 = mockMvc.perform(
                get(
                        URLUtils.builder(BASE_URL)
                                .addQueryParameter("city-name", "Es")
                                .build()
                )
        );

        response1.andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data", hasSize(1))
                );

        response2.andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data", hasSize(3))
                );

        response3.andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data", hasSize(4))
                );
    }
}
