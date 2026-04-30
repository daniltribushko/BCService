package ru.tdd.geo.application.services.imp.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tdd.core.controller.redis.kafka.KafkaService;
import ru.tdd.core.database.entities.kafka.OutboxEvent;
import ru.tdd.core.database.repositories.OutboxEventRepository;
import ru.tdd.geo.application.mappers.CountryMapper;
import ru.tdd.geo.application.models.enums.event.CountryOutboxEvent;
import ru.tdd.geo.database.entities.Country;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Сервис для отправки стран в кафку
 */
@Service
public class CountryKafkaService implements KafkaService<CountryOutboxEvent, Country> {

    private final OutboxEventRepository outboxEventRepository;

    private final CountryMapper countryMapper;

    private final ObjectMapper objectMapper;

    @Autowired
    public CountryKafkaService(
            OutboxEventRepository outboxEventRepository,
            CountryMapper countryMapper,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.countryMapper = countryMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(CountryOutboxEvent type, Country entity) {
        try {
            OutboxEvent event = new OutboxEvent(
                    Country.class.getName(),
                    type.getType(),
                    objectMapper.writeValueAsString(countryMapper.toDto(entity)),
                    LocalDateTime.now()
            );

            outboxEventRepository.save(event);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
