package ru.tdd.kafka_core.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.tdd.kafka_core.mappers.OutboxEventMapperImpl;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Конфигурация kafka с OutboxEvent
 */
@Configuration
@EntityScan("ru.tdd.kafka_core.entities")
@EnableJpaRepositories("ru.tdd.kafka_core.repository")
@Import(value = OutboxEventMapperImpl.class)
public class KafkaConfig {

}
