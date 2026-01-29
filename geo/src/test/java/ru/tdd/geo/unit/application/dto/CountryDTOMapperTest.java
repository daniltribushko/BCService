package ru.tdd.geo.unit.application.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.tdd.geo.application.models.dto.DTOMapper;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;

import java.time.ZoneId;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2025
 * Тесты преобразований и чтения json и модели страны
 */
class CountryDTOMapperTest {

    @Test
    void fromJsonTest() throws JsonProcessingException {
        CountryDTO actual = DTOMapper.fromJson(
                "{" +
                        "\"id\":\"e7b3c1f8-2a4d-4f6c-9b12-8d5e3a1c7f29\"," +
                        "\"name\":\"Test Mapper Country\"" +
                        "}",
                CountryDTO.class);

        Assertions.assertEquals(UUID.fromString("e7b3c1f8-2a4d-4f6c-9b12-8d5e3a1c7f29"), actual.getId());
        Assertions.assertEquals("Test Mapper Country", actual.getName());
    }

    @Test
    void toJsonTest() throws JsonProcessingException {

        String actual = DTOMapper.toJson(
                new CountryDTO(
                        UUID.fromString("93003560-55ab-421c-8a62-56a89aa69471"),
                        "Test Json Country"
                )
        );

        Assertions.assertEquals(
                "{" +
                        "\"id\":\"93003560-55ab-421c-8a62-56a89aa69471\"," +
                        "\"name\":\"Test Json Country\"" +
                        "}",
                actual
        );
    }
}
