package ru.tdd.geo.integrations.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.core.database.entities.kafka.OutboxEvent;
import ru.tdd.core.database.repositories.OutboxEventRepository;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.mappers.CountryMapper;
import ru.tdd.geo.application.models.enums.event.CountryOutboxEvent;
import ru.tdd.geo.application.services.imp.kafka.CountryKafkaService;
import ru.tdd.geo.database.entities.Country;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@DisplayName("Тестирование продюсера стран")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryKafkaServiceTest {

    private final ObjectMapper objectMapper;

    private final CountryMapper countryMapper;

    private final CountryKafkaService countryKafkaService;

    private final OutboxEventRepository outboxEventRepository;

    @Autowired
    CountryKafkaServiceTest(
            ObjectMapper objectMapper,
            CountryKafkaService countryKafkaService,
            OutboxEventRepository outboxEventRepository,
            CountryMapper countryMapper
        ) {
        this.objectMapper = objectMapper;
        this.countryKafkaService = countryKafkaService;
        this.outboxEventRepository = outboxEventRepository;
        this.countryMapper = countryMapper;
    }

    private static Stream<Arguments> sendEventTest() {
        Country country1 = new Country("Россия");
        country1.setId(UUID.randomUUID());

        Country country2 = new Country("Китай");
        country2.setId(UUID.randomUUID());

        return Stream.of(
                Arguments.arguments(
                        named("Проверка отправки события при создании страны", CountryOutboxEvent.CREATE),
                        country1
                ),
                Arguments.arguments(
                        named("Проверка отправки события при обновлении страны", CountryOutboxEvent.UPDATE),
                        country2
                ),
                Arguments.arguments(
                        named("Проверка отправки события при удалении страны", CountryOutboxEvent.DELETE),
                        country2
                )
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Тестирование отправки событий стран")
    void sendEventTest(CountryOutboxEvent event, Country country) throws Exception {
        countryKafkaService.send(
                event,
                country
        );

        OutboxEvent actual = outboxEventRepository.findAllByEntityName(Country.class.getName()).getLast();
        Assertions.assertEquals(event.getType(), actual.getEventType());
        Assertions.assertEquals(
                objectMapper.writeValueAsString(countryMapper.toDto(country)),
                actual.getPayload())
        ;
    }
}
