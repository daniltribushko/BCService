package ru.tdd.book.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Tribushko Danil
 * @since 02.06.2026
 * Dto создания жанра книги
 */
@Schema(description = "Dto запоса на создание жанра")
public class CreateGenreDto {

    @Schema(
            name = "name",
            description = "Название жанра",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    @NotBlank
    private String name;

    public CreateGenreDto() {}

    public CreateGenreDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
