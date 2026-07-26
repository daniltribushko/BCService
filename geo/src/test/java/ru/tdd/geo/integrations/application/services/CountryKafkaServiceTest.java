package ru.tdd.geo.integrations.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.proto.geo.CountryProto;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.mappers.CountryMapper;
import ru.tdd.geo.application.services.imp.kafka.CountryKafkaService;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.kafka_core.entities.OutboxEvent;
import ru.tdd.kafka_core.entities.OutboxEventType;
import ru.tdd.kafka_core.repository.OutboxEventRepository;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestcontainersConfiguration.class)
@DisplayName("Тестирование продюсера стран")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryKafkaServiceTest {
    private final CountryKafkaService countryKafkaService;

    private final OutboxEventRepository outboxEventRepository;

    @Autowired
    CountryKafkaServiceTest(
            CountryKafkaService countryKafkaService,
            OutboxEventRepository outboxEventRepository
    ) {
        this.countryKafkaService = countryKafkaService;
        this.outboxEventRepository = outboxEventRepository;
    }

    private static Stream<Arguments> sendEventTest() {
        Country country1 = new Country("Россия");
        country1.setId(UUID.randomUUID());

        Country country2 = new Country("Китай");
        country2.setId(UUID.randomUUID());

        return Stream.of(
                Arguments.arguments(
                        named("Проверка отправки события при создании страны", OutboxEventType.CREATE),
                        country1
                ),
                Arguments.arguments(
                        named("Проверка отправки события при обновлении страны", OutboxEventType.UPDATE),
                        country2
                ),
                Arguments.arguments(
                        named("Проверка отправки события при удалении страны", OutboxEventType.DELETE),
                        country2
                )
        );
    }

    @BeforeEach
    void cleanDb() {
        outboxEventRepository.deleteAll();
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Тестирование отправки событий стран")
    void sendEventTest(OutboxEventType event, Country country) throws Exception {
        countryKafkaService.send(
                event,
                country
        );

        CountryProto expected = CountryProto.newBuilder()
                .setId(country.getId().toString())
                .setName(country.getName())
                .build();

        OutboxEvent actualEvent = outboxEventRepository.findAllByEntityName(Country.class.getName()).stream().findFirst().get();
        CountryProto actual = CountryProto.parseFrom(actualEvent.getData());

        Assertions.assertEquals(event, actualEvent.getType());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }
}
