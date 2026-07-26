package ru.tdd.geo.application.services.imp.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tdd.bc.proto.geo.CountryProto;
import ru.tdd.geo.application.mappers.CountryMapper;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.kafka_core.entities.OutboxEvent;
import ru.tdd.kafka_core.entities.OutboxEventType;
import ru.tdd.kafka_core.repository.OutboxEventRepository;
import ru.tdd.kafka_core.services.KafkaService;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Сервис для отправки стран в кафку
 */
@Service
public class CountryKafkaService implements KafkaService<Country> {

    private final OutboxEventRepository outboxEventRepository;

    @Autowired
    public CountryKafkaService(
            OutboxEventRepository outboxEventRepository
    ) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Override
    public void send(OutboxEventType eventType, Country entity) {
        CountryProto proto = CountryProto.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .build();

        OutboxEvent event = new OutboxEvent(
                Country.class,
                proto.toByteArray(),
                eventType,
                1
        );

        outboxEventRepository.save(event);
    }
}
