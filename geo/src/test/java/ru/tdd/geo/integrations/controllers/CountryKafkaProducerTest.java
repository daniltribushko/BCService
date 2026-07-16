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
import ru.tdd.bc.proto.geo.CountryProto;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.controller.kafka.OutboxEventSender;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.kafka_core.dto.OutboxEventDto;
import ru.tdd.kafka_core.entities.OutboxEvent;
import ru.tdd.kafka_core.entities.OutboxEventType;
import ru.tdd.kafka_core.mappers.OutboxEventMapper;
import ru.tdd.kafka_core.repository.OutboxEventRepository;

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

        CountryProto country = CountryProto.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Россия")
                .build();

        outboxEventRepository.save(
                new OutboxEvent(
                        Country.class,
                        country.toByteArray(),
                        OutboxEventType.CREATE,
                       1
                )
        );

        outboxEventSender.sendCountryEvents();

        Thread.sleep(1000);

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000L));

        Assertions.assertTrue(records.iterator().hasNext());

        String message = records.iterator().next().value();
        OutboxEventDto event = objectMapper.readValue(message, OutboxEventDto.class);

        Assertions.assertEquals(OutboxEventType.CREATE, event.getType());
    }
}
