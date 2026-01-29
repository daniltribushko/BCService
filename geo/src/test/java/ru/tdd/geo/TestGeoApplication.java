package ru.tdd.geo;

import org.springframework.boot.SpringApplication;

public class TestGeoApplication {

    public static void main(String[] args) {
        SpringApplication.from(GeoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
