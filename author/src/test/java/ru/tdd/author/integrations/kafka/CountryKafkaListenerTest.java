package ru.tdd.author.integrations.kafka;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.author.TestcontainersConfiguration;
import ru.tdd.author.database.repositories.CountryRepository;

@SpringBootTest
@Testcontainers
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Тестирование консьюмера кафки стран")
public class CountryKafkaListenerTest {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final CountryRepository countryRepository;
}
