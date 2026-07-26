package ru.tdd.geo.controller.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.kafka_core.dto.OutboxEventDto;
import ru.tdd.kafka_core.entities.OutboxEvent;
import ru.tdd.kafka_core.mappers.OutboxEventMapper;
import ru.tdd.kafka_core.repository.OutboxEventRepository;

import java.util.Set;

@Component
public class OutboxEventSender {

    @Value("${apache-kafka.topics.country}")
    private String countryTopic;

    private final OutboxEventMapper outboxEventMapper;

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, OutboxEventDto> kafkaTemplate;

    public OutboxEventSender(
            OutboxEventMapper outboxEventMapper,
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, OutboxEventDto> kafkaTemplate
    ) {
        this.outboxEventMapper = outboxEventMapper;
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(cron = "30 * * * * *")
    public void sendCountryEvents() {
        Set<OutboxEvent> events = outboxEventRepository.findAllByEntityName(Country.class.getName());
        events.forEach(event -> {
            kafkaTemplate.send(countryTopic, outboxEventMapper.toDto(event));
            outboxEventRepository.delete(event);
        });
    }
}
