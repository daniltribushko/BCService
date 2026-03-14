package ru.tdd.geo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.UUID;

@EnableScheduling
@SpringBootApplication
public class GeoApplication {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
        SpringApplication.run(GeoApplication.class, args);
    }

}
