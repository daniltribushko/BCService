package ru.tdd.book.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Dto запроса на обновление жанра
 */
@Schema(description = "Dto запроса на обновление жанра")
public class UpdateGenreDto {

    @Schema(
            name = "name",
            description = "Название жанра",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            type = "string"
    )
    private String name;

    public UpdateGenreDto() {}

    public UpdateGenreDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
