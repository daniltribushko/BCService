package ru.tdd.geo.controller.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tdd.core.controller.dto.OutboxEventDTO;
import ru.tdd.core.controller.dto.OutboxEventMapper;
import ru.tdd.core.database.entities.kafka.OutboxEvent;
import ru.tdd.core.database.repositories.OutboxEventRepository;
import ru.tdd.geo.database.entities.Country;

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
    public void sendCountryEvents() {
        List<OutboxEvent> events = outboxEventRepository.findAllByEntityName(Country.class.getName());
        events.forEach(event -> {
            kafkaTemplate.send(countryTopic, outboxEventMapper.toDto(event));
            outboxEventRepository.delete(event);
        });
    }
}
