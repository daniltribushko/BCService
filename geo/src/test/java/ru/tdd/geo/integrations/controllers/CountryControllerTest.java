package ru.tdd.geo.integrations.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.DTOMapper;
import ru.tdd.geo.application.models.dto.geo.country.CreateCountryDTO;
import ru.tdd.geo.application.models.dto.geo.country.UpdateCountryDTO;
import ru.tdd.geo.application.utils.URLUtils;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CountryRepository;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Набор тестов контроллера стран
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ImportTestcontainers(value = TestcontainersConfiguration.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void cleanDb() {
        countryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createTest() throws Exception {
        CreateCountryDTO createDto = new CreateCountryDTO("New Country", ZoneId.systemDefault());

        ResultActions response = mockMvc.perform(post("/geo/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(DTOMapper.toJson(createDto))
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", isA(String.class)))
                .andExpect(jsonPath("$.name", is(createDto.getName())))
                .andExpect(jsonPath("$.zoneId", is(ZoneId.systemDefault().getId())));
    }

    @Test
    @WithMockUser(username = "not_admin")
    void createNotAdminFailTest() throws Exception {
        CreateCountryDTO createDto = new CreateCountryDTO("New Country", ZoneId.systemDefault());

        ResultActions response = mockMvc.perform(post("/geo/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(DTOMapper.toJson(createDto))
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createAlreadyExistsFailTest() throws Exception {
        countryRepository.save(new Country("Already Exists Country"));

        CreateCountryDTO createDto = new CreateCountryDTO("Already Exists Country", ZoneId.systemDefault());

        ResultActions response = mockMvc.perform(
                post("/geo/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(createDto))
        );

        response.andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.CONFLICT.value())))
                .andExpect(jsonPath("$.message", is("Страна с указанным названием уже создана")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateSuccessTest() throws Exception {
        Country country = new Country("Country For Update");

        countryRepository.save(country);

        ResultActions response = mockMvc.perform(
                put("/geo/countries/" + country.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateCountryDTO("New Country Name", null)))
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(country.getId().toString())))
                .andExpect(jsonPath("$.name", is("New Country Name")))
                .andExpect(jsonPath("$.zoneId", is(ZoneId.systemDefault().getId())));
    }

    @Test
    @WithMockUser(username = "user")
    void updateNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put("/geo/countries/" + UUID.randomUUID())
                        .content(DTOMapper.toJson(new UpdateCountryDTO(null, null)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put("/geo/countries/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateCountryDTO(null, null)))
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is("Страна с указанным идентификатором не найдена")));


    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateAlreadyExistsFailTest() throws Exception {
        Country country1 = new Country("Test Country");
        Country country2 = new Country("Already Exists Country");

        countryRepository.saveAll(List.of(country1, country2));

        ResultActions response = mockMvc.perform(
                put("/geo/countries/" + country1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateCountryDTO("Already Exists Country", null)))
        );

        response.andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.CONFLICT.value())))
                .andExpect(jsonPath("$.message", is("Страна с указанным названием уже создана")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteSuccessTest() throws Exception {
        Country country = new Country("Country For Delete");

        countryRepository.save(country);

        ResultActions response = mockMvc.perform(
                delete("/geo/countries/" + country.getId())
        );

        response.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete("/geo/countries/" + UUID.randomUUID())
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete("/geo/countries/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdSuccessTest() throws Exception {
        Country country = new Country("Test Country Find By Id");
        country.setZoneId(ZoneId.of("Europe/Moscow"));
        countryRepository.save(country);

        ResultActions response = mockMvc.perform(
                get("/geo/countries/" + country.getId())
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(country.getId().toString())))
                .andExpect(jsonPath("$.name", is("Test Country Find By Id")))
                .andExpect(jsonPath("$.zoneId", is("Europe/Moscow")));
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdNotFoundTest() throws Exception {
        ResultActions response = mockMvc.perform(
                get("/geo/countries/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void findAllSuccessTest() throws Exception {
        Country country1 = new Country("Россия");
        Country country2 = new Country("США");
        Country country3 = new Country("Руанда");
        Country country4 = new Country("Уганда");

        countryRepository.saveAll(List.of(country1, country2, country3, country4));

        ResultActions response1 = mockMvc.perform(
                get(
                        URLUtils.builder("/geo/countries/all")
                                .addQueryParameter("name", "аНДа", false)
                                .build()
                )
        );

        ResultActions response2 = mockMvc.perform(
                get(
                        URLUtils.builder("/geo/countries/all")
                                .addQueryParameter("page", 3, false)
                                .addQueryParameter("per_page", 1, false)
                                .build()
                )
        );

        ResultActions response3 = mockMvc.perform(
                get(
                        URLUtils.builder("/geo/countries/all")
                                .addQueryParameter("name", "А", false)
                                .build()
                )
        );

        response1.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));

        response2.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        response3.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));
    }
}
