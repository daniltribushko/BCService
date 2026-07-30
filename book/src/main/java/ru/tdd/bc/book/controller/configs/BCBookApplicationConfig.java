package ru.tdd.bc.book.controller.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.tdd.bc.config.EnableBcExceptionHandlers;
import ru.tdd.bc.database.config.EnableBCEntities;
import ru.tdd.bc.security.jwt.EnableBcJwtSecurity;
import ru.tdd.kafka_core.configs.EnableBcKafka;

@Configuration
@EnableBcKafka
@EnableBCEntities
@EnableScheduling
@EnableBcJwtSecurity
@EnableBcExceptionHandlers
@EntityScan(basePackages = {"ru.tdd.bc.book.database.entities"})
@EnableJpaRepositories({"ru.tdd.bc.book.database.repositories"})
public class BCBookApplicationConfig {
}
