package ru.tdd.book.controllers.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan({
        "ru.tdd.book.database.entities",
        "ru.tdd.core.database.entities"
})
@EnableJpaRepositories({
        "ru.tdd.book.database.repositories",
        "ru.tdd.core.database.repositories"
})
public class JpaConfig {
}
