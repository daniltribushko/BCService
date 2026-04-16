package ru.tdd.geo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.UUID;

@EnableScheduling
@EntityScan(basePackages = "ru.tdd")
@SpringBootApplication(
        scanBasePackages = "ru.tdd"
)
@EnableJpaRepositories("ru.tdd")
public class GeoApplication {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
        SpringApplication.run(GeoApplication.class, args);
    }

}
