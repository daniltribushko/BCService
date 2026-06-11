package ru.tdd.bc.dictionaries.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 */
@Schema(description = "Dto запроса на создание справочника")
public class CreateDictionaryDto {

    @NotBlank
    @Schema(
            name = "name",
            description = "Название справочника",
            type = "string",
            format = "Научная фантастика",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    public CreateDictionaryDto() {}

    public CreateDictionaryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
