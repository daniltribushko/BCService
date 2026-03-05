package ru.tdd.geo.controller.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tdd.geo.application.mappers.OutboxEventMapper;
import ru.tdd.geo.application.models.dto.OutboxEventDTO;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.OutboxEvent;
import ru.tdd.geo.database.repositories.OutboxEventRepository;

import java.util.List;

@Component
public class OutboxEventSender {

    @Value("${apache-kafka.topics.country}")
    private String countryTopic;

    private final OutboxEventMapper outboxEventMapper;

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, OutboxEventDTO> kafkaTemplate;

    public OutboxEventSender(
            OutboxEventMapper outboxEventMapper,
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, OutboxEventDTO> kafkaTemplate
    ) {
        this.outboxEventMapper = outboxEventMapper;
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(cron = "30 * * * * *")
    void sendCountryEvents() {
        List<OutboxEvent> events = outboxEventRepository.findAllByEntityName(Country.class.getName());
        events.forEach(event -> {
            kafkaTemplate.send(countryTopic, outboxEventMapper.toDto(event));
            outboxEventRepository.delete(event);
        });
    }
}
