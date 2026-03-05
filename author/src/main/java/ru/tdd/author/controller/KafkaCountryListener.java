package ru.tdd.author.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tdd.author.application.dto.OutboxEventDTO;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.enums.OutboxEventType;
import ru.tdd.author.application.enums.events.CountryOutboxEvent;
import ru.tdd.author.application.exceptions.country.CountryByIdNotFoundException;
import ru.tdd.author.application.mappers.CountryMapper;
import ru.tdd.author.application.utils.JsonUtils;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.CountryRepository;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.02.2026
 * Слушатель событий кафка стран
 */
@Component
public class KafkaCountryListener {

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    private final ObjectMapper objectMapper;

    public KafkaCountryListener(
            CountryRepository countryRepository,
            CountryMapper countryMapper,
            ObjectMapper objectMapper
    ) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${apache-kafka.topics.country}", groupId = "country")
    public void listenCountry(String data) throws JsonProcessingException {
        OutboxEventDTO event = objectMapper.readValue(data, OutboxEventDTO.class);

        switch (OutboxEventType.valueOf(event.getEventType(), CountryOutboxEvent.class)) {
            case CountryOutboxEvent.CREATE ->
                countryRepository.save(
                        countryMapper.toEntity(
                                JsonUtils.fromJson(event.getPayload(), CountryDTO.class)
                        )
                );
            case CountryOutboxEvent.UPDATE -> {
                CountryDTO countryDTO = JsonUtils.fromJson(event.getPayload(), CountryDTO.class);
                UUID countryId = countryDTO.getId();

                Country country = countryRepository.findById(countryId)
                        .orElseThrow(() -> new CountryByIdNotFoundException(countryId));
                country.setName(countryDTO.getName());

                countryRepository.save(country);
            }
            case CountryOutboxEvent.DELETE -> {
                CountryDTO countryDTO = JsonUtils.fromJson(event.getPayload(), CountryDTO.class);
                Country country = countryRepository.findById(countryDTO.getId())
                        .orElseThrow(() -> new CountryByIdNotFoundException(countryDTO.getId()));

                countryRepository.delete(country);
            }
        }
    }
}
