package ru.tdd.book.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * @author Tribusko Danil
 * @since 02.05.2026
 * Dto жанра книги
 */
public class GenreDto {

    @Schema(
            name = "id",
            description = "Идентификатор жанра",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            format = "uuid",
            example = ""
    )
    @JsonProperty(value = "id")
    private UUID id;

    @Schema(
            name = "name",
            description = "Название жанра",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            example = "Детектив"
    )
    @JsonProperty(value = "name")
    private String name;

    public GenreDto() {}

    public GenreDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
