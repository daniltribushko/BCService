package ru.tdd.author;

import org.springframework.boot.SpringApplication;

public class TestAuthorApplication {

    public static void main(String[] args) {
        SpringApplication.from(AuthorApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
