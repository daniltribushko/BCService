package ru.tdd.geo.integrations.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import ru.tdd.core.controller.dto.OutboxEventDTO;
import ru.tdd.core.controller.dto.OutboxEventMapper;
import ru.tdd.core.database.entities.kafka.OutboxEvent;
import ru.tdd.core.database.repositories.OutboxEventRepository;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.application.models.enums.event.CountryOutboxEvent;
import ru.tdd.geo.controller.kafka.OutboxEventSender;
import ru.tdd.geo.database.entities.Country;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@DisplayName("Продюсер стран")
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryKafkaProducerTest {

    @Value("${spring.kafka.group}")
    private String kafkaGroup;

    @Value("${apache-kafka.topics.country}")
    private String countryTopic;

    private final OutboxEventRepository outboxEventRepository;

    private final OutboxEventSender outboxEventSender;

    private final ObjectMapper objectMapper;

    private final KafkaContainer kafkaContainer;

    private Consumer<String, String> consumer;

    @Autowired
    CountryKafkaProducerTest(
            OutboxEventRepository outboxEventRepository,
            OutboxEventSender outboxEventSender,
            ObjectMapper objectMapper,
            OutboxEventMapper outboxEventMapper,
            KafkaContainer kafkaContainer
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxEventSender = outboxEventSender;
        this.objectMapper = objectMapper;
        this.kafkaContainer = kafkaContainer;
    }

    void setConsumer() {
        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers()
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                kafkaGroup
        );

        consumer = new KafkaConsumer<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        );

        consumer.subscribe(List.of(countryTopic));
    }

    @Test
    void testProducer() throws JsonProcessingException, InterruptedException {
        setConsumer();

        CountryDTO country = new CountryDTO(
            UUID.randomUUID(),
                "Россия"
        );

        outboxEventRepository.save(
                new OutboxEvent(
                        Country.class.getName(),
                        CountryOutboxEvent.CREATE.getType(),
                        objectMapper.writeValueAsString(
                            country
                        ),
                        LocalDateTime.now()
                )
        );

        outboxEventSender.sendCountryEvents();

        Thread.sleep(1000);

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000L));

        Assertions.assertTrue(records.iterator().hasNext());

        String message = records.iterator().next().value();;
        OutboxEventDTO event = objectMapper.readValue(message, OutboxEventDTO.class);

        Assertions.assertEquals(CountryOutboxEvent.CREATE.getType(), event.getEventType());
    }
}
