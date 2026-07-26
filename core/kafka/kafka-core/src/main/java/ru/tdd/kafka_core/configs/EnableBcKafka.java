package ru.tdd.kafka_core.configs;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Аннотация для включения бинов для работы кафки
 */
@Documented
@Import(KafkaConfig.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableBcKafka {
}
