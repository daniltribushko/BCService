package ru.tdd.bc.book.integrations.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.book.TestcontainersConfiguration;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.mappers.PublisherMapper;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.database.entities.Publisher;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 06.08.2026
 */
@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@DisplayName("Тестирование маппера издателей")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PublisherMapperTest {

    @Autowired
    private PublisherMapper publisherMapper;

    @Test
    @DisplayName("Преобразование в ДТО")
    void toDtoTest() {
        UUID publisherId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Publisher entity = Publisher.builder()
                .id(publisherId)
                .name("АСТ")
                .url("https://ast.ru")
                .country(new Country(countryId, "Россия"))
                .build();

        PublisherDTO actual = publisherMapper.toDto(entity);

        LocalDate now = LocalDate.now();

        Assertions.assertEquals(publisherId, actual.getId());
        Assertions.assertEquals("АСТ", actual.getName());
        Assertions.assertEquals("https://ast.ru", actual.getUrl());
        Assertions.assertEquals(countryId, actual.getCountry().getId());
        Assertions.assertEquals(now, actual.getCreationTime().toLocalDate());
        Assertions.assertEquals(now, actual.getUpdateTime().toLocalDate());
    }
}
