package ru.tdd.geo.integrations.controllers;

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
import ru.tdd.geo.application.models.dto.geo.region.CreateRegionDTO;
import ru.tdd.geo.application.models.dto.geo.region.UpdateRegionDTO;
import ru.tdd.geo.application.utils.URLUtils;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 10.01.2026
 * Набор тестов контроллера по работе с регионами
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ImportTestcontainers(value = TestcontainersConfiguration.class)
class RegionControllerTest {

    private final MockMvc mockMvc;

    private final CountryRepository countryRepository;

    private final RegionRepository regionRepository;

    @Autowired
    RegionControllerTest(MockMvc mockMvc, CountryRepository countryRepository, RegionRepository regionRepository) {
        this.mockMvc = mockMvc;
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
    }

    @BeforeEach
    void cleanDb() {
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveSuccessTest() throws Exception {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        CreateRegionDTO dto = new CreateRegionDTO("New Region", country.getId());

        ResultActions response = mockMvc.perform(
                post("/geo/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(dto))
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.country.id", is(country.getId().toString())));
    }

    @Test
    @WithMockUser(username = "user")
    void saveNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post("/geo/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new CreateRegionDTO("Region For Create", UUID.randomUUID())))
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveAlreadyExistsFailTest() throws Exception {
        Country country = new Country("Russia");

        countryRepository.save(country);

        Region region = new Region("Moscow Oblast", country);

        regionRepository.save(region);

        CreateRegionDTO dto = new CreateRegionDTO("Moscow Oblast", country.getId());

        ResultActions response = mockMvc.perform(
                post("/geo/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(dto))
        );

        response.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Регион с указанным названием и страной уже создан")
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateSuccessTest() throws Exception {
        Country country1 = new Country("Russia");
        Country country2 = new Country("China");

        countryRepository.saveAll(List.of(country1, country2));

        Region region1 = new Region("Region 1", country1);
        Region region2 = new Region("Region 2", country1);

        regionRepository.saveAll(List.of(region1, region2));

        UpdateRegionDTO dto1 = new UpdateRegionDTO("New Region", null);
        UpdateRegionDTO dto2 = new UpdateRegionDTO(null, country2.getId());
        UpdateRegionDTO dto3 = new UpdateRegionDTO("Updated Region 2", country2.getId());

        ResultActions response1 = mockMvc.perform(
                put("/geo/regions/" + region1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(dto1))
        );

        ResultActions response2 = mockMvc.perform(
                put("/geo/regions/" + region1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(dto2))
        );

        ResultActions response3 = mockMvc.perform(
                put("/geo/regions/" + region2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(dto3))
        );

        response1.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(region1.getId().toString())))
                .andExpect(jsonPath("$.name", is("New Region")));

        response2.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(region1.getId().toString())))
                .andExpect(jsonPath("$.country.id", is(country2.getId().toString())));

        response3.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(region2.getId().toString())))
                .andExpect(jsonPath("$.name", is("Updated Region 2")))
                .andExpect(jsonPath("$.country.id", is(country2.getId().toString())));
    }

    @Test
    @WithMockUser(username = "user")
    void updateNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put("/geo/regions/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateRegionDTO("Region", UUID.randomUUID())))
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRegionNotFoundTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put("/geo/regions/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateRegionDTO(null, null)))
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Регион с указанным идентификатором не найден")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCountryNotFoundTest() throws Exception {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        Region region = new Region("Test Region", country);

        regionRepository.save(region);

        ResultActions response = mockMvc.perform(
                put("/geo/regions/" + region.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateRegionDTO(null, UUID.randomUUID())))
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Страна с указанным идентификатором не найдена")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateAlreadyExistsFailTest() throws Exception {
        Country country1 = new Country("Test Country 1");
        Country country2 = new Country("Test Country 2");

        countryRepository.saveAll(List.of(country1, country2));

        Region region1 = new Region("Region 1", country1);
        Region region2 = new Region("Already Exists Region", country1);
        Region region3 = new Region("Already Exists Region", country2);

        regionRepository.saveAll(List.of(region1, region2, region3));

        ResultActions response1 = mockMvc.perform(
                put("/geo/regions/" + region1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateRegionDTO("Already Exists Region", null)))
        );

        ResultActions response2 = mockMvc.perform(
                put("/geo/regions/" + region1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DTOMapper.toJson(new UpdateRegionDTO("Already Exists Region", country2.getId())))
        );

        response1.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Регион с указанным названием и страной уже создан")));

        response2.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Регион с указанным названием и страной уже создан")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteSuccessTest() throws Exception {
        Country country = new Country("Country 1");

        countryRepository.save(country);

        Region region = new Region("Region For Delete", country);

        regionRepository.save(region);

        ResultActions response = mockMvc.perform(
                delete("/geo/regions/" + region.getId())
        );

        response.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete("/geo/regions/" + UUID.randomUUID())
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete("/geo/regions/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Регион с указанным идентификатором не найден")));
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdSuccessTest() throws Exception {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        Region region1 = new Region("Test Region 1", country);
        Region region2 = new Region("Test Region 2", country);
        Region region3 = new Region("Test Region 3", country);

        regionRepository.saveAll(List.of(region1, region2, region3));

        ResultActions response = mockMvc.perform(
                get("/geo/regions/" + region2.getId())
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(region2.getName())));
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdNotFoundFail() throws Exception {
        ResultActions response = mockMvc.perform(
                get("/geo/regions/" + UUID.randomUUID())
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Регион с указанным идентификатором не найден")));
    }

    @Test
    @WithMockUser(username = "username")
    void findAllTest() throws Exception {
        Country country1 = new Country("cOUNtry TeSt");
        Country country2 = new Country("TESting");
        Country country3 = new Country("CoUnTrY");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Region region1 = new Region("TesT rEgIoN", country2);
        Region region2 = new Region("RWte", country1);
        Region region3 = new Region("obLast", country3);
        Region region4 = new Region("tEsTiNg", country1);
        Region region5 = new Region("MOScow OblASt", country2);

        regionRepository.saveAll(List.of(region1, region2, region3, region4, region5));

        ResultActions response1 = mockMvc.perform(
                get(
                        URLUtils.builder("/geo/regions/all")
                                .addQueryParameter("country-name", "TEST", false)
                                .addQueryParameter("name", "OBLAST", false)
                                .build()
                )
        );

        ResultActions response2 = mockMvc.perform(
                get(
                        URLUtils.builder("/geo/regions/all")
                                .addQueryParameter("country-name", "CoUnTrY", false)
                                .build()
                )
        );

        ResultActions response3 = mockMvc.perform(
          get(
                  URLUtils.builder("/geo/regions/all")
                          .addQueryParameter("name", "tE", false)
                          .build()
          )
        );

        response1.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        response2.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));

        response3.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));
    }
}
